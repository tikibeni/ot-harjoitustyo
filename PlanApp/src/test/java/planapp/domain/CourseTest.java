package planapp.domain;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class CourseTest {
    
    @Test
    public void notSameCourse() {
        Course c1 = new Course("TKTTEST", "Course Test");
        Course c2 = new Course("TKTDIFF", "Different Course");
        
        assertFalse(c1.equals(c2));
    }
    
    @Test
    public void differentObjects(){
        Course c = new Course("TKTTEST", "Course Testing");
        Object x = new Object();
        assertFalse(c.equals(x));
    }
    
    @Test
    public void contructorWorksCorrectly() {
        Course c = new Course("TKTTEST", "Course Testing 101");
        
        assertEquals("TKTTEST: Course Testing 101", c.toString());
    }
    
    @Test
    public void coursePrerequisites() {
        Course c = new Course("TKTTEST", "Course Testing");
        c.addPrerequisite(new Course("TKTPRE", "Required Course"));
        
        List<Course> pre = c.getPrerequisites();
        assertEquals("TKTPRE: Required Course", pre.get(0).toString());
        
    }
    
    @Test
    public void constructorCourseCode() {
        Course c = new Course("TKTTEST", "Course Testing");
        
        assertEquals("TKTTEST", c.getCourseCode());
    }
    
    @Test
    public void constructorCourseName() {
        Course c = new Course("TKTTEST", "Course Testing");
        assertEquals("Course Testing", c.getCourseName());
    }    
}
