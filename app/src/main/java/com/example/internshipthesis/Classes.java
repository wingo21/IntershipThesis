package com.example.internshipthesis;

import java.util.Date;

/**
 * This Class helps to manage the database and pull information from it correctly inside
 * other activities
 */

public class Classes {

    // User class (used for database management)

    public static class User {

        private String username;
        private String email;
        private String password;

        public User() {}

        public User(String username, String email, String password) {

            this.username = username;
            this.email = email;
            this.password = password;
        }

        public String getUsername() {return username;}
        public String getPassword() {return password;}
        public String getEmail() {return email;}

        public void setUsername(String username) {this.username = username;}
        public void setEmail(String email) {this.email = email;}
        public void setPassword(String password) {this.password = password;}
    }

    // Worker class (used for database management)

    public static class Worker {

        private String name;
        private float rating;
        private String slot;

        public Worker() {}

        public Worker(String name, float rating, String slot) {

            this.name = name;
            this.rating = rating;
            this.slot = slot;
        }

        public String getName() {return name;}
        public float getRating() {return rating;}
        public String getSlot() {return slot;}

        public void setName(String name) {this.name = name;}
        public void setRating(float rating) {this.rating = rating;}
        public void setSlot(String slot) {this.slot = slot;}
    }

    // Schedules class (used for database management)

    public static class Schedules {

        private String workerID;
        private Date slot;

        public String getWorkerID() {return workerID;}
        public Date getSlot() {return slot;}

        public void setWorkerID(String workerID) {this.workerID = workerID;}
        public void setSlot(Date slot) {this.slot = slot;}
    }
}
