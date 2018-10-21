package com.elsevier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.elsevier.entities.CollaboratorInfo;
import com.elsevier.utils.CurriculumUtils;
import com.elsevier.utils.GitHubProperties;

public class CurriculumProcessor {
	
	private RepositoryService repositoryService;
	private CollaboratorService collaboratorService;
	private CommitService commitService;
	
	public CurriculumProcessor() {
		//
	}
	
	public CurriculumProcessor(RepositoryService repostioryService, CollaboratorService collaboratorServvice, CommitService commitService) {
		this.repositoryService = repostioryService;
		this.collaboratorService = collaboratorServvice;
		this.commitService = commitService;
	}

	/**
	 * Outputs all the GitHub repositories of a certain user, listing collaborators for each repository.
	 * @param userName GitHub user
	 * @param token authentication token for GitHub API v3
	 */
	public void processUserRepos(String userName, String token) {
		if (userName == null || userName.isEmpty()) {
			throw new IllegalArgumentException("Please provide a github username");
		}
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Please provide an authentication token");
		}
		GitHubClient client = createClient(token);
		createServices(client);
		Map<Repository, List<CollaboratorInfo>> userRepos = getUserRepos(userName, client);
		output(userName, userRepos);
	}
	
	private GitHubClient createClient(@Nonnull String token) {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(token);
		return client;
	}
	
	private void createServices(GitHubClient client) {
		repositoryService = new RepositoryService(client);
		collaboratorService = new CollaboratorService(client);
		commitService = new CommitService(client);
	}
	
	public GitHubClient init(String token) {
		GitHubClient client = createClient(token);
		createServices(client);
		return client;
	}
	
	public Map<Repository, List<CollaboratorInfo>> getUserRepos(@Nonnull String userName, @Nonnull GitHubClient client) {
		List<Repository> repositories;
		try {
			repositories = repositoryService.getRepositories(userName);
		} catch (IOException e) {
			CurriculumUtils.msg("Exception while repositories for user: " + userName + " Error: " + e.getMessage());
			return null;
		}
		Map<Repository, List<CollaboratorInfo>> userRepos = new HashMap<>();
		for (Repository repository : repositories) {
			// process only public repositories
			if (repository.isPrivate()) {
				continue;
			}
			List<CollaboratorInfo> collaboratorsInfo = listRepoCollaboratorsCommits(client, repository);
			userRepos.put(repository, collaboratorsInfo);
		}
		return userRepos;
	}
	
	/**
	 * 
	 * @param client the GitHubClient to use
	 * @param repository the repository to analyze
	 * @return list of collaborators of a repository ordered by the number of commits of the user to the repository.
	 */
	private List<CollaboratorInfo> listRepoCollaboratorsCommits(@Nonnull GitHubClient client, @Nonnull Repository repository) {
		List<User> collaborators;
		List<RepositoryCommit> commits;
		try {
			collaborators = collaboratorService.getCollaborators(repository);
			commits = commitService.getCommits(repository);
		} catch (IOException e1) {
			CurriculumUtils.msg("Exception while getting collaborators/commits for repository: " + repository.getName()	+ " Error: " + e1.getMessage());
			return null;
		}
		return collaborators.stream()
				            .map(collaborator -> new CollaboratorInfo(collaborator, countCollaboratorCommits(collaborator, commits)))
				            .sorted()
				            .collect(Collectors.toList());
	}
	
	private long countCollaboratorCommits(User collaborator, List<RepositoryCommit> commits) {
		return commits.stream()
				 	  .filter(commit -> commit.getCommit().getAuthor().getName().equals(collaborator.getLogin()))
				 	  .count();
	}
	
	private void output(String userName, Map<Repository, List<CollaboratorInfo>> userRepos) {
		CurriculumUtils.msg("------");
		CurriculumUtils.msg("User: " + userName);
		CurriculumUtils.msg("------");

		userRepos.entrySet().forEach(e -> {
			CurriculumUtils.msg("Repository: " + e.getKey().getName());
			if (e.getValue() == null) {
				CurriculumUtils.msg("Not possible to access to the repository's collaborators.");
				return;
			}
			e.getValue().stream().forEach(c -> CurriculumUtils.msg(c.toString()));
		});
	}

	public static void main(String[] args) {
		CurriculumProcessor cp = new CurriculumProcessor();
		if (args.length != 1) {
			throw new IllegalArgumentException("Please provide a github username");
		}
		String userName = args[0];
		String token = System.getenv(GitHubProperties.AUTH_TOKEN);
		if (token == null) {
			throw new IllegalArgumentException("Please set the environment variable " + GitHubProperties.AUTH_TOKEN + " before starting the application.");
		}
		cp.processUserRepos(userName, token);
	}
}