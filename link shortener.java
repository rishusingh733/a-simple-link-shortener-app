import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class LinkShortenerApp {
    private static final LinkShortener linkShortener = new LinkShortener();
    private static final Database database = new Database();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    shortenURL();
                    break;
                case 2:
                    expandURL();
                    break;
                case 0:
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("===== URL Shortener Menu =====");
        System.out.println("1. Shorten a URL");
        System.out.println("2. Expand a shortened URL");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void shortenURL() {
        System.out.print("Enter the URL to shorten: ");
        String originalURL = scanner.nextLine();

        if (!isValidURL(originalURL)) {
            System.out.println("Invalid URL format. Please enter a valid URL.");
            return;
        }

        String shortURL = linkShortener.shortenURL(originalURL);
        System.out.println("Shortened URL: " + shortURL);
        database.save(shortURL.substring(LinkShortener.BASE_URL.length()), originalURL);
    }

    private static void expandURL() {
        System.out.print("Enter the shortened URL to expand: ");
        String shortURL = scanner.nextLine();

        String expandedURL = linkShortener.expandURL(shortURL);
        if (expandedURL.equals("Shortened URL not found")) {
            System.out.println("Shortened URL not found.");
        } else {
            System.out.println("Expanded URL: " + expandedURL);
        }
    }

    private static boolean isValidURL(String url) {
        // Very basic URL format check
        return url.startsWith("http://") || url.startsWith("https://");
    }
}

class LinkShortener {
    private Map<String, String> shortToLongMap;
    private Map<String, String> longToShortMap;
    static final String BASE_URL = "https://short.url/";
    private static final int SHORT_KEY_LENGTH = 7;

    public LinkShortener() {
        this.shortToLongMap = new HashMap<>();
        this.longToShortMap = new HashMap<>();
    }

    // Method to generate a random short URL key
    private String generateShortKey() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    // Method to shorten a given URL
    public String shortenURL(String longURL) {
        if (longToShortMap.containsKey(longURL)) {
            return BASE_URL + longToShortMap.get(longURL);
        } else {
            String shortKey = generateShortKey();
            shortToLongMap.put(shortKey, longURL);
            longToShortMap.put(longURL, shortKey);
            return BASE_URL + shortKey;
        }
    }

    // Method to expand a shortened URL
    public String expandURL(String shortURL) {
        String shortKey = shortURL.substring(BASE_URL.length());
        if (shortToLongMap.containsKey(shortKey)) {
            return shortToLongMap.get(shortKey);
        } else {
            return "Shortened URL not found";
        }
    }
}

class Database {
    private Map<String, String> shortToLongMap;
    private Map<String, String> longToShortMap;

    public Database() {
        this.shortToLongMap = new HashMap<>();
        this.longToShortMap = new HashMap<>();
    }

    public void save(String shortKey, String longURL) {
        shortToLongMap.put(shortKey, longURL);
        longToShortMap.put(longURL, shortKey);
    }

    public String getLongURL(String shortKey) {
        return shortToLongMap.get(shortKey);
    }

    public String getShortKey(String longURL) {
        return longToShortMap.get(longURL);
    }

    public boolean containsShortKey(String shortKey) {
        return shortToLongMap.containsKey(shortKey);
    }

    public boolean containsLongURL(String longURL) {
        return longToShortMap.containsKey(longURL);
    }
}