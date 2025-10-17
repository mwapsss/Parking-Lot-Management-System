import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class Parking {

    static String REPORT_FILE = "report.txt"; // database file creatation and opening
    static List<ParkingRecord> records = new ArrayList<>(); // creataed new list array for parking records with variable records
    static Scanner input = new Scanner(System.in); // static scanner for global usage
    static boolean running = true;

    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // global date format 
    static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH); // global time format

    public static void main(String[] args) {

        // prints menu
        // loop ends when e is not picked by user
        while (!choice.contains("e") {
            System.out.println("\n--- PARKING LOT MANAGEMENT SYSTEM ---");
            System.out.println("a. View All Vehicles");
            System.out.println("b. Park a Vehicle");
            System.out.println("c. Remove a Vehicle");
            System.out.println("d. Generate Report");
            System.out.println("e. Exit");
            System.out.print("Enter choice: ");
            String choice = input.nextLine().toLowerCase();

            // validate user choice
            switch (choice) {
                case "a":
                    viewAllVehicles();
                    break;
                case "b":
                    parkVehicle();
                    break;
                case "c":
                    removeVehicle();
                    break;
                case "d":
                    generateReport();
                    break;

                    // if user enters an input that is not inside the choices
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // view all vehicles method
    static void viewAllVehicles() {

        // check if list array is empty
        if (records.isEmpty()) {
            System.out.println("\nNo vehicles are currently parked."); // output if list array is empty
            return; // return to menu
        }

        int count = 1; // counting in formatting the table

        // keep looping inside the records list
        for (ParkingRecord record : records) {
            String date = record.timeIn.format(dateFormat); // formatting for date
            String time = record.timeIn.format(timeFormat); // formatting for time

            // prints out table format
            System.out.println(count + ". " + record.plateNumber + " | " + record.vehicleType +
                    " | Slot: " + record.parkingSlot + " | Date: " + date + " | Time-in: " + time);
            count++; // adds 1 count every new line
        }
    }

    //method for parking vehicles
    static void parkVehicle() {

        // used try catch for date and time formatting
        try {

            // ask user for plate number
            System.out.print("\nEnter Plate Number: ");
            String plate = input.nextLine().trim();

            // if input is empty then this condition continues
            if (plate.isEmpty()) {
                System.out.println("Invalid plate number!");
                return; // return to menu
            }

            // prompt for vehicel type
            System.out.print("Enter Vehicle Type (Car, Van, Motorcycle): ");
            String type = input.nextLine().trim();

            // condition to only restrict car, van, and motorcycle as the only choices available
            if (!(type.equalsIgnoreCase("Car") ||
                  type.equalsIgnoreCase("Van") ||
                  type.equalsIgnoreCase("Motorcycle"))) {
                System.out.println("Invalid vehicle type! Must be Car, Van, or Motorcycle.");
                return; // return to menu
            }

            // ask user for parking slot
            System.out.print("Enter Parking Slot: ");
            String slot = input.nextLine().trim();

            // condition to check if input is empty
            if (slot.isEmpty()) {
                System.out.println("Invalid slot!");
                return; // return to menu
            }

            // user input for time in
            System.out.print("Enter Time-in (Example: 8:00 AM): ");
            String timeInStr = input.nextLine().trim();

            // check if input is empty
            if (timeInStr.isEmpty()) {
                System.out.println("Invalid time-in!");
                return; // return
            }

            DateTimeFormatter tFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime timeIn = LocalTime.parse(timeInStr, tFormat);
            LocalDateTime dateTimeIn = LocalDateTime.of(LocalDate.now(), timeIn);

            // loops inside list array and looks for this objects
            ParkingRecord record = new ParkingRecord();
            record.plateNumber = plate;
            record.vehicleType = type;
            record.parkingSlot = slot;
            record.timeIn = dateTimeIn;
            records.add(record);

            System.out.println("Vehicle parked successfully!");

            // error handling
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format! Please use format like '8:00 AM'."); // output if time format is incorrect
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    // method for removing vehicle
    static void removeVehicle() {

        // used try-catch for error handling
        try {

            // ask user for palte number to remove
            System.out.print("\nEnter Plate Number to remove: ");
            String plate = input.nextLine().trim();

            // loop that scans the list array to check for that plate number
            ParkingRecord found = null;
            for (ParkingRecord record : records) {

                // if plate number is found proceeds to remove
                if (record.plateNumber.equalsIgnoreCase(plate)) {
                    found = record;
                    break;
                }
            }

            // if plate number is not found then this conditon executes
            if (found == null) {
                System.out.println("Vehicle not found!");
                return; // return to menu
            }

            // ask user for timeout
            System.out.print("Enter Time-out (Example: 10:00 PM): ");
            String timeOutStr = input.nextLine().trim();

            // check if input is empty
            if (timeOutStr.isEmpty()) {
                System.out.println("Invalid time-out!");
                return; // return to menu
            }

            DateTimeFormatter tFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime timeOut = LocalTime.parse(timeOutStr, tFormat);
            found.timeOut = LocalDateTime.of(LocalDate.now(), timeOut);

            long hours = Duration.between(found.timeIn, found.timeOut).toHours();
            if (hours <= 0) hours = 1;
            found.hoursParked = (int) hours;

            // calculate the fee
            double rate;
            if (found.vehicleType.equalsIgnoreCase("Motorcycle")) {
                rate = 10;
            } else if (found.vehicleType.equalsIgnoreCase("Van")) {
                rate = 20;
            }

            found.fee = found.hoursParked * rate; // calculate the fee based on how long its parked

            // system receipt when vehicle is succesfully removed from from database
            System.out.println("Vehicle removed successfully!");
            System.out.println("Hours Parked: " + found.hoursParked);
            System.out.println("Parking Fee: ₱" + found.fee);

            // handling error for time formatting
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format! Please use format like '10:00 PM'.");
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    // method for generating report
    static void generateReport() {
        List<ParkingRecord> completed = new ArrayList<>(); // create new array list with variable completed

        // loops inside the new array list
        for (ParkingRecord record : records) {

            // if a vehicle has a time-out saved then it adds it onto the new completed array list
            if (record.timeOut != null) {
                completed.add(record);
            }
        }

        // if no vehicle found that is outside of parking then this condition works
        if (completed.isEmpty()) {
            System.out.println("\nNo completed parking records to report.");
            return; // return to menu
        }

        double totalFees = 0;
        int totalVehicles = 0;

        // parking report table
        System.out.println("\n--- PARKING REPORT ---");
        System.out.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                "#", "Date", "Time-in", "Plate No.", "Type", "Hours", "Fee");

        int count = 1; // counting for the format table 

        // loops inside the array list
        for (ParkingRecord record : completed) {
            String date = record.timeIn.format(dateFormat); // formatting date
            String timeIn = record.timeIn.format(timeFormat); // formatting time
            System.out.printf("%-3d %-12s %-10s %-12s %-12s %-7d %-10.2f%n",
                    count++, date, timeIn, record.plateNumber, record.vehicleType,
                    record.hoursParked, record.fee);
            totalVehicles++; // adds to the current vehicles inside the parking slot
            totalFees += record.fee; // adds the sum and inputs it into totalFees
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.println("Total Fees Collected: ₱" + totalFees);

        saveReport(completed, totalVehicles, totalFees);
    }

    static void saveReport(List<ParkingRecord> completed, int totalVehicles, double totalFees) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_FILE))) {
            writer.println("--- PARKING REPORT ---");
            writer.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                    "#", "Date", "Time-in", "Plate No.", "Type", "Hours", "Fee");

            int count = 1;
            for (ParkingRecord record : completed) {
                String date = record.timeIn.format(dateFormat);
                String timeIn = record.timeIn.format(timeFormat);
                writer.printf("%-3d %-12s %-10s %-12s %-12s %-7d %-10.2f%n",
                        count++, date, timeIn, record.plateNumber, record.vehicleType,
                        record.hoursParked, record.fee);
            }

            writer.println("--------------------------------------------");
            writer.println("Total Vehicles: " + totalVehicles);
            writer.println("Total Fees Collected: ₱" + totalFees);
            System.out.println("Report saved to " + REPORT_FILE);
        } catch (IOException e) {
            System.out.println("Error saving report file.");
        }
    }
}

class ParkingRecord {
    String plateNumber;
    String vehicleType;
    String parkingSlot;
    int hoursParked;
    double fee;
    LocalDateTime timeIn;
    LocalDateTime timeOut;
}
