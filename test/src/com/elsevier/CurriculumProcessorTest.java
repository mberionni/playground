package com.elsevier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.elsevier.entities.CollaboratorInfo;
import com.elsevier.utils.GitHubProperties;

/* 
 * These tests only run successfully with my token
 */

public class CurriculumProcessorTest {
	private static String token;
	
	@BeforeAll
	public static void init() throws Exception {
	    token = System.getenv(GitHubProperties.AUTH_TOKEN);
		if (token == null) {
			throw new Exception("Please set the environment variable " + GitHubProperties.AUTH_TOKEN);
		}
	}
	
	@Test
	public void testInputValidation() {
		CurriculumProcessor cp = new CurriculumProcessor();
		Assertions.assertThrows(IllegalArgumentException.class, () -> cp.processUserRepos(null, null));
		Assertions.assertThrows(IllegalArgumentException.class, () -> cp.processUserRepos(null, "aaa"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> cp.processUserRepos("", "aaa"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> cp.processUserRepos("aaa", ""));
	}
	
	@Disabled
	@Test
	public void testUserNameProcess() {
		CurriculumProcessor cp = new CurriculumProcessor();
		cp.processUserRepos("mberionni", token);
		cp.processUserRepos("mloop1", token);
	}
	
	@Disabled
	@Test
	public void getUserRepos() {
		CurriculumProcessor cp = new CurriculumProcessor();
		GitHubClient client = cp.init(token);
		Map<Repository, List<CollaboratorInfo>> userRepos = cp.getUserRepos("mberionni", client);
		for (Entry<Repository, List<CollaboratorInfo>> entry : userRepos.entrySet() ) {
			String repoName = entry.getKey().getName(); 
			List<CollaboratorInfo> collaboratorsInfo = entry.getValue();
			assertNotNull(collaboratorsInfo);
			switch (repoName) {
				case "webcrawler":
					assertEquals(collaboratorsInfo.size(), 2, "2 collaborators expected");
					assertEquals(collaboratorsInfo.get(0).getCollaborator().getLogin(), "mloop1", "expected collaborator is mloop1");
					assertEquals(collaboratorsInfo.get(0).getCommits(), 6, "6 commits expected");
					assertEquals(collaboratorsInfo.get(1).getCollaborator().getLogin(), "mberionni", "expected collaborator is mberionni");
					assertEquals(collaboratorsInfo.get(1).getCommits(), 1, "1 commit expected");
					break;
				case "Dijkstra2000":
					assertEquals(collaboratorsInfo.size(), 2, "2 collaborators expected");
					assertEquals(collaboratorsInfo.get(0).getCollaborator().getLogin(), "mloop1", "expected collaborator is mloop1");
					assertEquals(collaboratorsInfo.get(0).getCommits(), 5, "5 commits expected");
					assertEquals(collaboratorsInfo.get(1).getCollaborator().getLogin(), "mberionni", "expected collaborator is mberionni");
					assertEquals(collaboratorsInfo.get(1).getCommits(), 4, "4 commit expected");
					break;
				case "uni":
					assertEquals(collaboratorsInfo.size(), 2, "2 collaborators expected");
					assertEquals(collaboratorsInfo.get(0).getCollaborator().getLogin(), "mberionni", "expected collaborator is mberionni");
					assertEquals(collaboratorsInfo.get(0).getCommits(), 4, "4 commits expected");
					assertEquals(collaboratorsInfo.get(1).getCollaborator().getLogin(), "mloop1", "expected collaborator is mloop1");
					assertEquals(collaboratorsInfo.get(1).getCommits(), 0, "0 commit expected");
					break;
			}
		}
	}
}