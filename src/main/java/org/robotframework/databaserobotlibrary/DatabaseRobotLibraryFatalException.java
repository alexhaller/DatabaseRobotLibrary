package org.robotframework.databaserobotlibrary;

@SuppressWarnings("serial")
public class DatabaseRobotLibraryFatalException extends RuntimeException {
	/**
	 * Non fatal exception
	 */
	public static final boolean ROBOT_EXIT_ON_FAILURE = true;

	public DatabaseRobotLibraryFatalException() {
		super();
	}

	public DatabaseRobotLibraryFatalException(String string) {
		super(string);
	}
}