package com.elsevier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.elsevier.entities.CollaboratorInfo;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class CurriculumProcessorTestMock {
	
	@Mock
	private  GitHubClient client;
	@Mock
	private  GitHubResponse response;
	@Mock
	private RepositoryService repositoryService;
	@Mock
	private  CollaboratorService collaboratorService;
	@Mock
	private  CommitService commitService;
	
	private static final String REPO_NAME = "web1";
	private static final String COLLABO_NAME = "mloop1";

	@BeforeEach
	public void beforeEach() throws IOException {
		List<Repository> listRepositories = new ArrayList<>();
		List<User> listCollaborators = new ArrayList<>();
		List<RepositoryCommit> listRepositoryCommits = new ArrayList<>();
     	
		User user = new User();
		user.setName("michele");
		user.setLogin("mberionni");
		
		Repository repo = new Repository();
		repo.setName(REPO_NAME);
		repo.setPrivate(false);
		repo.setOwner(user);
		listRepositories.add(repo);
		
		User collaborator = new User();
		collaborator.setName(COLLABO_NAME);
		collaborator.setLogin(COLLABO_NAME);
		listCollaborators.add(collaborator);

		RepositoryCommit repoCommit = new RepositoryCommit();
		repoCommit.setAuthor(collaborator);
		repoCommit.setSha("1234");
		Commit commit = new Commit();
		CommitUser commitUser = new CommitUser();
		commitUser.setName(collaborator.getLogin());
		commit.setAuthor(commitUser);
		repoCommit.setCommit(commit);
		listRepositoryCommits.add(repoCommit);
		
		when(repositoryService.getRepositories(user.getLogin())).thenReturn(listRepositories);
		when(collaboratorService.getCollaborators(repo)).thenReturn(listCollaborators);
		when(commitService.getCommits(repo)).thenReturn(listRepositoryCommits);
	}
	
	@Test
	public void getUserReposMock() {
		CurriculumProcessor cp = new CurriculumProcessor(repositoryService, collaboratorService, commitService);
		client.setOAuth2Token("aaaa");
		Map<Repository, List<CollaboratorInfo>> userRepos = cp.getUserRepos("mberionni", client);
		for (Entry<Repository, List<CollaboratorInfo>> entry : userRepos.entrySet() ) {
			String repoName = entry.getKey().getName(); 
			List<CollaboratorInfo> collaboratorsInfo = entry.getValue();
			assertNotNull(collaboratorsInfo);
			switch (repoName) {
				case REPO_NAME:
					assertEquals(collaboratorsInfo.size(), 1, "1 collaborator expected");
					assertEquals(collaboratorsInfo.get(0).getCollaborator().getLogin(), COLLABO_NAME, "expected collaborator is " + COLLABO_NAME);
					assertEquals(collaboratorsInfo.get(0).getCommits(), 1, "1 commit expected");
					break;
				default:
					fail("Unexpected repository name " + repoName);
					break;
			}
		}
	}
}