import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class Parking {

    static String REPORT_FILE = "report.txt";
    static List<ParkingRecord> records = new ArrayList<>();
    static Scanner input = new Scanner(System.in);
    static boolean running = true;

    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    public static void main(String[] args) {
        while (running) {
            System.out.println("\n--- PARKING LOT MANAGEMENT SYSTEM ---");
            System.out.println("a. View All Vehicles");
            System.out.println("b. Park a Vehicle");
            System.out.println("c. Remove a Vehicle");
            System.out.println("d. Generate Report");
            System.out.println("e. Exit");
            System.out.print("Enter choice: ");
            String choice = input.nextLine().toLowerCase();

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
                case "e":
                    System.out.println("Thank you!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void viewAllVehicles() {
        if (records.isEmpty()) {
            System.out.println("\nNo vehicles are currently parked.");
            return;
        }

        int count = 1;
        for (ParkingRecord record : records) {
            String date = record.timeIn.format(dateFormat);
            String time = record.timeIn.format(timeFormat);
            System.out.println(count + ". " + record.plateNumber + " | " + record.vehicleType +
                    " | Slot: " + record.parkingSlot + " | Date: " + date + " | Time-in: " + time);
            count++;
        }
    }

    static void parkVehicle() {
        try {
            System.out.print("\nEnter Plate Number: ");
            String plate = input.nextLine().trim();
            if (plate.isEmpty()) {
                System.out.println("Invalid plate number!");
                return;
            }

            System.out.print("Enter Vehicle Type (Car, Van, Motorcycle): ");
            String type = input.nextLine().trim();
            if (!(type.equalsIgnoreCase("Car") ||
                  type.equalsIgnoreCase("Van") ||
                  type.equalsIgnoreCase("Motorcycle"))) {
                System.out.println("Invalid vehicle type! Must be Car, Van, or Motorcycle.");
                return;
            }

            System.out.print("Enter Parking Slot: ");
            String slot = input.nextLine().trim();
            if (slot.isEmpty()) {
                System.out.println("Invalid slot!");
                return;
            }

            System.out.print("Enter Time-in (Example: 8:00 AM): ");
            String timeInStr = input.nextLine().trim();
            if (timeInStr.isEmpty()) {
                System.out.println("Invalid time-in!");
                return;
            }

            DateTimeFormatter tFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime timeIn = LocalTime.parse(timeInStr, tFormat);
            LocalDateTime dateTimeIn = LocalDateTime.of(LocalDate.now(), timeIn);

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

    static void removeVehicle() {
        try {
            System.out.print("\nEnter Plate Number to remove: ");
            String plate = input.nextLine().trim();

            ParkingRecord found = null;
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

            double rate;
            if (found.vehicleType.equalsIgnoreCase("Motorcycle")) {
                rate = 10;
            } else if (found.vehicleType.equalsIgnoreCase("Van")) {
                rate = 20;
            } else {
                rate = 20;
            }

            found.fee = found.hoursParked * rate;

            System.out.println("Vehicle removed successfully!");
            System.out.println("Hours Parked: " + found.hoursParked);
            System.out.println("Parking Fee: ₱" + found.fee);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format! Please use format like '10:00 PM'.");
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    static void generateReport() {
        List<ParkingRecord> completed = new ArrayList<>();
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
