package utils;

import models.*;
import play.Logger;

import java.util.ArrayList;

public class DemoData {

    public static User user1;
    public static User user2;

    public static Todo todo1_1;
    public static Todo todo1_2;

    public static Todo todo2_1;

    public static void loadDemoData() {
        
        Logger.info("Loading Demo Data");

        user1 = new User("ben.immanuel@aistemos.com", "ben", "Ben Immanuel");
        user1.save();
        
        todo1_1 = new Todo(user1, "make it secure");
        todo1_1.save();

        todo1_2 = new Todo(user1, "make it neat");
        todo1_2.save();

        user2 = new User("steve.harris@aistemos.com", "steve", "Steve Harris");
        user2.save();

        todo2_1 = new Todo(user2, "make it pretty");
        todo2_1.save();
    }

}
