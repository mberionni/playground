package com.elsevier.entities;

import org.eclipse.egit.github.core.User;

public class CollaboratorInfo implements Comparable<CollaboratorInfo> {
	private final User collaborator;
	private final long commits;
	
	public CollaboratorInfo(User collaborator, long commits) {
		this.collaborator = collaborator;
		this.commits = commits;
	}

	@Override
	public int compareTo(CollaboratorInfo other) {
		return -1 * ((this.commits < other.commits) ? -1 : ((this.commits == other.commits) ? 0 : 1));
	}

	public User getCollaborator() {
		return collaborator;
	}

	public long getCommits() {
		return commits;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("--- collaborator login id=")
		  .append(collaborator.getLogin())
		  .append(" name=")
		  .append(collaborator.getName())
		  .append(", commits=")
		  .append(commits)
		  .append("]");
		return sb.toString();
	}
}