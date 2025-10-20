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
        String choice = "";
        while (!choice.equals("e")) {
            System.out.println("\n--- PARKING LOT MANAGEMENT SYSTEM ---");
            System.out.println("a. View All Vehicles");
            System.out.println("b. Park a Vehicle");
            System.out.println("c. Remove a Vehicle");
            System.out.println("d. Generate Report");
            System.out.println("e. Exit");
            System.out.print("Enter choice: ");
            choice = input.nextLine().toLowerCase();

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
                default:
                    if (!choice.equals("e")) {
                        System.out.println("Invalid choice. Please try again.");
                    }
            }
        }
    }

    // view all vehicles method
    static void viewAllVehicles() {

        // check if list array is empty
        if (records.isEmpty()) {
            System.out.println("\nNo vehicles are currently parked.");
            return;
        }

        int count = 1;

        // display all records in table format
        for (ParkingRecord record : records) {
            String date = record.timeIn.format(dateFormat);
            String time = record.timeIn.format(timeFormat);
            System.out.println(count + ". " + record.plateNumber + " | " + record.vehicleType +
                    " | Slot: " + record.parkingSlot + " | Date: " + date + " | Time-in: " + time);
            count++;
        }
    }

    // method for parking vehicles
    static void parkVehicle() {
        try {
            // ask user for plate number
            System.out.print("\nEnter Plate Number: ");
            String plate = input.nextLine().trim();

            if (plate.isEmpty()) {
                System.out.println("Invalid plate number!");
                return;
            }

            // ask user for vehicle type
            System.out.print("Enter Vehicle Type (Car, Van, Motorcycle): ");
            String type = input.nextLine().trim();

            if (!(type.equalsIgnoreCase("Car") ||
                  type.equalsIgnoreCase("Van") ||
                  type.equalsIgnoreCase("Motorcycle"))) {
                System.out.println("Invalid vehicle type! Must be Car, Van, or Motorcycle.");
                return;
            }

            // ask user for parking slot
            System.out.print("Enter Parking Slot: ");
            String slot = input.nextLine().trim();

            if (slot.isEmpty()) {
                System.out.println("Invalid slot!");
                return;
            }

            // ask user for time-in
            System.out.print("Enter Time-in (Example: 8:00 AM): ");
            String timeInStr = input.nextLine().trim();

            if (timeInStr.isEmpty()) {
                System.out.println("Invalid time-in!");
                return;
            }

            DateTimeFormatter tFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime timeIn = LocalTime.parse(timeInStr, tFormat);
            LocalDateTime dateTimeIn = LocalDateTime.of(LocalDate.now(), timeIn);

            // store record data
            ParkingRecord record = new ParkingRecord();
            record.plateNumber = plate;
            record.vehicleType = type;
            record.parkingSlot = slot;
            record.timeIn = dateTimeIn;
            records.add(record);

            System.out.println("Vehicle parked successfully!");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format! Please use format like '8:00 AM'.");
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    // method for removing vehicle
    static void removeVehicle() {
        try {
            // ask user for plate number
            System.out.print("\nEnter Plate Number to remove: ");
            String plate = input.nextLine().trim();

            ParkingRecord found = null;

            // find record in list
            for (ParkingRecord record : records) {
                if (record.plateNumber.equalsIgnoreCase(plate)) {
                    found = record;
                    break;
                }
            }

            if (found == null) {
                System.out.println("Vehicle not found!");
                return;
            }

            // ask user for time-out
            System.out.print("Enter Time-out (Example: 10:00 PM): ");
            String timeOutStr = input.nextLine().trim();

            if (timeOutStr.isEmpty()) {
                System.out.println("Invalid time-out!");
                return;
            }

            DateTimeFormatter tFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime timeOut = LocalTime.parse(timeOutStr, tFormat);
            found.timeOut = LocalDateTime.of(LocalDate.now(), timeOut);

            long hours = Duration.between(found.timeIn, found.timeOut).toHours();
            if (hours <= 0) hours = 1;
            found.hoursParked = (int) hours;

            // calculate fee
            double rate = 0;
            if (found.vehicleType.equalsIgnoreCase("Motorcycle")) {
                rate = 10;
            } else if (found.vehicleType.equalsIgnoreCase("Van")) {
                rate = 20;
            } else if (found.vehicleType.equalsIgnoreCase("Car")) {
                rate = 15;
            }

            found.fee = found.hoursParked * rate;

            // display result
            System.out.println("Vehicle removed successfully!");
            System.out.println("Hours Parked: " + found.hoursParked);
            System.out.println("Parking Fee: ₱" + found.fee);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format! Please use format like '10:00 PM'.");
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    // method for generating report
    static void generateReport() {
        List<ParkingRecord> completed = new ArrayList<>();

        // filter completed records
        for (ParkingRecord record : records) {
            if (record.timeOut != null) {
                completed.add(record);
            }
        }

        if (completed.isEmpty()) {
            System.out.println("\nNo completed parking records to report.");
            return;
        }

        double totalFees = 0;
        int totalVehicles = 0;

        // print report table
        System.out.println("\n--- PARKING REPORT ---");
        System.out.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                "#", "Date", "Time-in", "Plate No.", "Type", "Hours", "Fee");

        int count = 1;

        for (ParkingRecord record : completed) {
            String date = record.timeIn.format(dateFormat);
            String timeIn = record.timeIn.format(timeFormat);
            System.out.printf("%-3d %-12s %-10s %-12s %-12s %-7d %-10.2f%n",
                    count++, date, timeIn, record.plateNumber, record.vehicleType,
                    record.hoursParked, record.fee);
            totalVehicles++;
            totalFees += record.fee;
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.println("Total Fees Collected: ₱" + totalFees);

        saveReport(completed, totalVehicles, totalFees);
    }

    // method for saving report
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
