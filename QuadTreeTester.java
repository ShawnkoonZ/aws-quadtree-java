/**
 * Tester File.
 * Composed with User logics.
 */

// Utils
import java.io.IOException;

// Version Imports
// import version1.*;
import version2.*;

public class QuadTreeTester {
  public static void main(String[] args) throws IOException {
    double xLow = 0;
    double yLow = 0;
    double xHigh = 8;
    double yHigh = 8;
    double minimumGap = 1;
    int partitionLimit = 10;

    QuadTree tree = new QuadTree(xLow, yLow, xHigh, yHigh, minimumGap);
    tree.setBucketName("tf-quadtree-main-bucket");
    tree.setFilePrefix("aws");
    tree.setFileExtension("csv");

    System.out.println("=> Process begin...");
    tree.generateQuadTree(partitionLimit);
    System.out.println("=> Tree generating process finished...");
  }
}
