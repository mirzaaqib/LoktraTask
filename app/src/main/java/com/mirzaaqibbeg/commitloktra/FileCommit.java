package com.mirzaaqibbeg.commitloktra;

/**
 * Created by Mirzaaqibbeg on 07-06-2016.
 */
public class FileCommit {
    public static final String N_SHA="sha";
    public static final String N_COMMIT_MESSAGE="message";
    public static final String N_AUTHOR="author";
    public static final String N_COMMIT="commit";
    public static final String N_AUTHOR_NAME="name";

    private String commitSHA;
    private String commitMessage;
    private String commitAuthorName;

    public String getCommitSHA() {
        return commitSHA;
    }

    public void setCommitSHA(String commitSHA) {
        this.commitSHA = commitSHA;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getCommitAuthorName() {
        return commitAuthorName;
    }

    public void setCommitAuthorName(String commitAuthorName) {
        this.commitAuthorName = commitAuthorName;
    }
}

