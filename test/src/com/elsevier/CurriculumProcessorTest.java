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
 * The disabled test only runs successfully with my token
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
					assertEquals(2, collaboratorsInfo.size(), "2 collaborators expected");
					assertEquals("mloop1", collaboratorsInfo.get(0).getCollaborator().getLogin(), "expected collaborator ");
					assertEquals(6, collaboratorsInfo.get(0).getCommits(), "commits expected");
					assertEquals("mberionni", collaboratorsInfo.get(1).getCollaborator().getLogin(), "expected collaborator");
					assertEquals(0, collaboratorsInfo.get(1).getCommits(), "commits expected");
					break;
				case "Dijkstra2000":
					assertEquals(2, collaboratorsInfo.size(), "2 collaborators expected");
					assertEquals("mloop1", collaboratorsInfo.get(0).getCollaborator().getLogin(), "expected collaborator");
					assertEquals(5, collaboratorsInfo.get(0).getCommits(), "commits expected");
					assertEquals("mberionni", collaboratorsInfo.get(1).getCollaborator().getLogin(), "expected collaborator");
					assertEquals(0, collaboratorsInfo.get(1).getCommits(), "commits expected");
					break;
				case "uni":
					assertEquals(2, collaboratorsInfo.size(), "2 collaborators expected");
					assertEquals("mberionni", collaboratorsInfo.get(0).getCollaborator().getLogin(), "expected collaborator");
					assertEquals(2, collaboratorsInfo.get(0).getCommits(), "commits expected");
					assertEquals("mloop1", collaboratorsInfo.get(1).getCollaborator().getLogin(), "expected collaborator ");
					assertEquals(0, collaboratorsInfo.get(1).getCommits(), "commits expected");
					break;
			}
		}
	}
}