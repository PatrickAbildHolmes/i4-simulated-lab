//package dk.g4.st25.database;
//
//import org.junit.jupiter.api.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class DatabaseTest {
//    private static final Database db = Database.getDB();
//
//    @BeforeEach
//    void setUp() {
//        db.reset();
//    }
//
//    @Test
//    void getAmount() {
//        DronePart part = DronePart.PROPELLER;
//        int amount = db.getAmount(part);
//        assertEquals(100, amount, "Amount for " + part.getItemName() + " should be the 100 by default.");
//    }
//
//    @Test
//    void setAmount() {
//        DronePart part = DronePart.PROPELLER;
//        db.setAmount(part,50);
//        int amount = db.getAmount(part);
//        assertEquals(50, amount, "Amount for " + part.getItemName() + " should be the 50 after calling setAmount(part, 50).");
//    }
//
//    @Test
//    void addAmount() {
//        DronePart part = DronePart.PROPELLER;
//        db.addAmount(part,5);
//        int amount = db.getAmount(part);
//        assertEquals(105, amount, "Amount for " + part.getItemName() + " should be the 105 after calling addAmount(part, 5).");
//    }
//}
