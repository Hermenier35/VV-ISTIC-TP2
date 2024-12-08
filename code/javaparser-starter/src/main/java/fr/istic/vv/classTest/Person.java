package fr.istic.vv.classTest;

    public class Person {
        private int age;
        private String name;
        private double height;
        private String firstName;

        public String getName() { return name; }

        public boolean isAdult() {
            return age > 17;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

