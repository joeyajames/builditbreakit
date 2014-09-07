//package logread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class logread {
    String password;
    boolean outputFormat; // true = stdout; false = html
    ArrayList<String> employeesInGallery, guestsInGallery;
    String name;
    boolean guestType; // true = guest; false = employee
    int[] listOfRooms;
    int lowerTime1 = -1, upperTime1 = -1, lowerTime2, upperTime2;
    boolean timeBounds; // true = -A; false = -B
    int roomNumber;
    String filename;
    String encryptionKey = "spider";
    String[] in;
    ArrayList<Event> eventList;
    
    public static void main(String[] args) {
        logread log = new logread();
        log.eventList = new ArrayList<>();
        log.employeesInGallery = new ArrayList<>();
        log.guestsInGallery = new ArrayList<>();
        //log.in = new String[] {"-K", "secret", "-S", "log1.txt"}; // for testing purposes
        //log.parseInput(log.in); 
		log.parseInput(args);
        log.readLog();
        log.getGalleryState();
        Collections.sort(log.employeesInGallery);
        for (int i=0; i<log.employeesInGallery.size(); i++)
            System.out.print(log.employeesInGallery.get(i) + ", ");
        System.out.println("");
        Collections.sort(log.guestsInGallery);
        for (int i=0; i<log.guestsInGallery.size(); i++)
            System.out.print(log.guestsInGallery.get(i) + ", ");
    }
    
    // read and verify pw from log file, then if correct, read rest of log into eventList
    public void readLog() {
        String encryptedPasswordRead, decryptedPassword;
        String nextLine;
        try {
            File f = new File (filename);
            if(f.exists()) {
                FileReader fr = new FileReader(filename);
                BufferedReader br = new BufferedReader(fr);
                encryptedPasswordRead = br.readLine();
                //System.out.println(encryptedPasswordRead.length());
                decryptedPassword = decryptPassword(encryptedPasswordRead);
                if (decryptedPassword.equals(password)) { // read data in
                    for (nextLine = br.readLine(); nextLine != null; nextLine = br.readLine())
                        eventList.add(parseLine(nextLine));
                }
                else System.out.println("error: pw mismatch");
            }
        }
        catch (IOException e) {System.out.println("IO error.");}
            
    }
    
    // parse a line (string) from the log into a new Event
    // -T time, -K password, -E employee name, -G guest name, -A/L arrive/leave, -R room
    public Event parseLine(String line) {
        String name1 = "";
        boolean type1 = false; // true = guest; false = employee
        int room1 = 0;
        int time1 = 0;
        boolean arrive1 = false;
        String[] s = line.split(" "); // convert string to array of strings
        
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("-T")) {
                try {
                    time1 = Integer.parseInt(s[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + s[0] + " must be an integer.");
                    System.exit(1);
                }
            }
            if (s[i].equals("-E")) {
                name1 = s[i+1];
                type1 = false; }
            if (s[i].equals("-G")) {
                name1 = s[i+1];
                type1 = true; }
            if (s[i].equals("-A")) {
                arrive1 = true;}
            if (s[i].equals("-L")) {
                arrive1 = false; }
            if (s[i].equals("-R")) {
                try {
                    room1 = Integer.parseInt(s[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + s[0] + " must be an integer.");
                    System.exit(1);
                }
            }
        }
        return new Event (name1, type1, room1, time1, arrive1);
    }
    
    public String decryptPassword (String pw) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pw.length(); i++) {
            sb.append(Character.toChars(((pw.charAt(i) - encryptionKey.charAt(i%encryptionKey.length())) + 128) % 128));
        }
        return sb.toString();
    }
    
    // parse command line args input
    // -T time, -K password, -E employee name, -G guest name, 
    // -A/B timebounds A/B, -U/L upper/lower time bounds, -R room
    public void parseInput (String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-L")) {
                try {
                    if (lowerTime1 == -1)
                        lowerTime1 = Integer.parseInt(args[i+1]);
                    else 
                        lowerTime2 = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + args[i] + " must be an integer.");
                    System.exit(1);
                }
            }
            if (args[i].equals("-U")) {
                try {
                    if (upperTime1 == -1)
                        upperTime1 = Integer.parseInt(args[i+1]);
                    else 
                        upperTime2 = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + args[i] + " must be an integer.");
                    System.exit(1);
                }
            }
            if (args[i].equals("-K")) // password
                 password = args[i+1];
            if (args[i].equals("-S")) // stdout
                outputFormat = true;
            if (args[i].equals("-H")) // html output
                outputFormat = false;
            if (args[i].equals("-E")) {
                name = args[i+1];
                guestType = false; }
            if (args[i].equals("-G")) {
                name = args[i+1];
                guestType = true; }
            if (args[i].equals("-A")) {
                timeBounds = true;}
            if (args[i].equals("-B")) {
                timeBounds = false; }
            if (args[i].equals("-R")) {
                try {
                    roomNumber = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + args[0] + " must be an integer.");
                    System.exit(1);
                }
            }
        }
        filename = args[args.length - 1];
    }
    
    public void getGalleryState() {
        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            if (e.type == true) {
                if (!guestsInGallery.contains(e.name))
                    guestsInGallery.add(e.name);
            }
            else if (!employeesInGallery.contains(e.name))
                    employeesInGallery.add(e.name);
        }
    }
    
    public class Event {
        String name;
        boolean type; // true = guest; false = employee
        int roomNumber;
        int time;
        boolean arrive; // true = arrive; false = leave
        
        // constructor
        public Event(String _name, boolean _type, int _roomNumber, int _time, boolean _arrive) {
            name = _name;
            type = _type;
            roomNumber = _roomNumber;
            time = _time;
            arrive = _arrive;
        }
    }

}
