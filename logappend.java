// CS-152 Build-it Break-it

//package logappend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class logappend {
    private int time;
    private String password;
    private String name;
    private boolean guestType; // true = guest; false = employee
    private boolean arrive; // true = arrive; false = leave
    private int roomNumber;
    private String filename;
    private String stringToSave;
    String[] in;
    String encryptionKey = "spider";
    String encryptedPassword;

    public static void main(String[] args) {
        logappend log = new logappend();
        //log.in = new String[]{"-T", "1", "-K", "secret", "-A", "-E", "Fred", "-R", "5", "log1.txt"};
        log.stringToSave = "";
        //log.parseInput(log.in);
        log.parseInput(args);
		log.encryptPassword();
        if (log.checkPassword())
            log.writeToLog();
    }
    
    // parse command line input and assign values to variables
    // -T time, -K password, -E employee name, -G guest name, -A/L arrive/leave, -R room
    public void parseInput (String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-T")) {
                stringToSave = stringToSave + args[i] + " " + args[i+1];
                try {
                    time = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + args[i] + " must be an integer.");
                    System.exit(1);
                }
            }
            if (args[i].equals("-K")) 
                 password = args[i+1];
            if (args[i].equals("-E")) {
                stringToSave = stringToSave + " " + args[i] + " " + args[i+1]; 
                name = args[i+1];
                guestType = false; }
            if (args[i].equals("-G")) {
                stringToSave = stringToSave + " " + args[i] + " " + args[i+1];
                name = args[i+1];
                guestType = true; }
            if (args[i].equals("-A")) {
                stringToSave = stringToSave + " " + args[i];
                arrive = true;}
            if (args[i].equals("-L")) {
                stringToSave = stringToSave + " " + args[i];
                arrive = false; }
            if (args[i].equals("-R")) {
                try {
                    stringToSave = stringToSave + " " + args[i] + " " + args[i+1] + "\n";
                    roomNumber = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + args[i] + " must be an integer.");
                    System.exit(1);
                }
            }
        }
        filename = args[args.length - 1];
    }
    
    // check password against recorded password
    public boolean checkPassword() {
        String encryptedPasswordRead, decryptedPassword;
        try {
            File f = new File (filename);
            if(f.exists()) {
                FileReader fr = new FileReader(filename);
                BufferedReader br = new BufferedReader(fr);
                encryptedPasswordRead = br.readLine();
                decryptedPassword = decryptPassword(encryptedPasswordRead);
                if (!decryptedPassword.equals(password)) return false;
            }
            return true;
        }
        catch (IOException e) {System.out.println("IO error.");}
            
        return false;
    }
    
    public String decryptPassword (String pw) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pw.length(); i++) {
            sb.append(Character.toChars(((pw.charAt(i) - encryptionKey.charAt(i%encryptionKey.length())) + 128) % 128));
        }
        return sb.toString();
    }
    
    public void encryptPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            sb.append (Character.toChars((password.charAt(i) + encryptionKey.charAt
                    (i%encryptionKey.length()))%128));
        }
        encryptedPassword = sb.toString();
    }
    
    // check if input is valid based on current state of gallery
    public boolean validateInput() {
        return false;
    }
    
    // write data to log file
    public void writeToLog() {
        System.out.println(stringToSave);
        File f = new File(filename);
        boolean writePassword = false;
        if(!f.exists()) writePassword = true;
        //File file = new File(filename);
            try {
                FileWriter writer = new FileWriter (filename, true);
                if (writePassword) writer.write(encryptedPassword + "\n");
                writer.write(stringToSave);
                writer.close();
            }
        catch (IOException e) {System.out.println("IO error.");}
    }
}
