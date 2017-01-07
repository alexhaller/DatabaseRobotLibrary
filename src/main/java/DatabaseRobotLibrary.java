import org.robotframework.javalib.library.AnnotationLibrary;

//Reference: http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html
public class DatabaseRobotLibrary extends AnnotationLibrary {
	// Possible values: "TEST CASE", "TEST SUITE", "GLOBAL"
	public static final String ROBOT_LIBRARY_SCOPE = "TEST CASE";
	public static final String ROBOT_LIBRARY_VERSION = "1.0";
	public static final String ROBOT_LIBRARY_DOC_FORMAT = "HTML";
	public static final String KEYWORD_PATTERN = "org/robotframework/databaserobotlibrary/**/*.class";

	public DatabaseRobotLibrary() {
		super(KEYWORD_PATTERN);
	}
}