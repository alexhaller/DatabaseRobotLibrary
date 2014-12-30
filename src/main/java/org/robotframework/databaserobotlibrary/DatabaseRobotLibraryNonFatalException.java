package org.robotframework.databaserobotlibrary;

@SuppressWarnings("serial")
public class DatabaseRobotLibraryNonFatalException extends RuntimeException {
	/**
	 * Non fatal exception
	 */
	public static final boolean ROBOT_EXIT_ON_FAILURE = false;

	public DatabaseRobotLibraryNonFatalException() {
		super();
	}

	public DatabaseRobotLibraryNonFatalException(String string) {
		super(string);
	}
}