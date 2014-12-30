import org.robotframework.javalib.library.AnnotationLibrary;

public class DatabaseRobotLibrary extends AnnotationLibrary {
	// Reference: http://robotframework.googlecode.com/hg/doc/userguide/RobotFrameworkUserGuide.html
	// Possible values: "TEST CASE", "TEST SUITE", "GLOBAL"
	public static final String ROBOT_LIBRARY_SCOPE = "TEST CASE";
	public static final String ROBOT_LIBRARY_VERSION = "1.0";
	public static final String ROBOT_LIBRARY_DOC_FORMAT = "HTML";
	public static final String KEYWORD_PATTERN = "org/robotframework/databaserobotlibrary/**/*.class";

	public DatabaseRobotLibrary() {
		super(KEYWORD_PATTERN);
	}
}