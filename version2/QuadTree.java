/*
*  -------------
* |  Q2  |  Q1  |
* |-------------|
* |  Q3  |  Q4  |
*  -------------
*/
package version2;

import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;

public class QuadTree {
   private TreeNode rootNode;
   private double minimumGap;
   private int partitionLimit, nodeCounter, leafCounter, fileCounter, numberOfNodes, numberOfFiles, numberOfLeaves;
   private FileUtil treeFileBuilder;
   private String[] nodeCounterInBinary = {"00", "01", "10", "11"};
   private String startingBinary, filePrefix, fileExtension;

   private void init(double xLow, double yLow, double xHigh, double yHigh, double minimumGap) {
     this.nodeCounter = 0;
     this.leafCounter = 0;
     this.startingBinary = "";
     this.setFilePrefix("aws");
     this.setFileExtension("csv");
     this.setPartitionLimit(10);
     this.rootNode = new TreeNode(xLow, yLow, xHigh, yHigh, startingBinary, this.nodeCounter);
     this.treeFileBuilder = new FileUtil(this.filePrefix);
     this.minimumGap = minimumGap;
     this.checkForExit();
     this.numberOfFiles = this.treeFileBuilder.calculateNumberOfFiles(this.numberOfNodes, this.partitionLimit);
   }

   public QuadTree() {
     this.init(0,0,8,8,1);
   }

   public QuadTree(double xLow, double yLow, double xHigh, double yHigh, double minimumGap) {
     this.init(xLow, yLow, xHigh, yHigh, minimumGap);
   }
   
   public void setFileExtension(String extension){
      this.fileExtension = extension;
   }
   
   public void setFilePrefix(String prefix){
      this.filePrefix = prefix;
   }
   
   public void setPartitionLimit(int upperBound){
      this.partitionLimit = upperBound;
   }
   
   public void generateQuadTree(int partitionLimit) throws IOException {
      Queue<TreeNode> treeQueue = new LinkedList<TreeNode>();
      treeQueue.add(this.rootNode);
      TreeNode curNode = treeQueue.poll();

      while(curNode != null) {
         if (!curNode.isLeafNode()) {
            // Q1
            treeQueue.add(
               new TreeNode(
               (curNode.xHigh - curNode.xLow)/2 + curNode.xLow,
               (curNode.yHigh - curNode.yLow)/2 + curNode.yLow,
               curNode.xHigh,
               curNode.yHigh,
               curNode.binaryIndex + nodeCounterInBinary[0],
               (curNode.numberIndex * nodeCounterInBinary.length) + 1
               )
            );
            // Q2
            treeQueue.add(
               new TreeNode(
               curNode.xLow,
               (curNode.yHigh - curNode.yLow)/2 + curNode.yLow,
               (curNode.xHigh - curNode.xLow)/2  + curNode.xLow,
               curNode.yHigh,
               curNode.binaryIndex + nodeCounterInBinary[1],
               (curNode.numberIndex * nodeCounterInBinary.length) + 2
               )
            );
            // Q3
            treeQueue.add(
               new TreeNode(
               curNode.xLow,
               curNode.yLow,
               (curNode.xHigh - curNode.xLow)/2 + curNode.xLow,
               (curNode.yHigh - curNode.yLow)/2 + curNode.yLow,
               curNode.binaryIndex + nodeCounterInBinary[2],
               (curNode.numberIndex * nodeCounterInBinary.length) + 3
               )
            );
            // Q4
            treeQueue.add(
               new TreeNode(
               (curNode.xHigh - curNode.xLow)/2 + curNode.xLow,
               curNode.yLow,
               curNode.xHigh,
               (curNode.yHigh - curNode.yLow)/2 + curNode.yLow,
               curNode.binaryIndex + nodeCounterInBinary[3],
               (curNode.numberIndex * nodeCounterInBinary.length) + 4
               )
            );
         }
         else {
            System.out.print("\n LEAF ! ");
            String fileNode = this.buildNodeForFile(curNode.binaryIndex, curNode.xLow, curNode.yLow, curNode.xHigh, curNode.yHigh);
            this.leafCounter++;
            this.numberOfLeaves++;
          
            try{
               this.treeFileBuilder.bufferAdd(fileNode);
               
               if(this.leafCounter == this.partitionLimit || this.nodeCounter == this.numberOfNodes){
                  this.fileCounter++;
                  this.treeFileBuilder.buildFile(this.fileExtension, this.fileCounter);
                  this.treeFileBuilder.bufferClear();
                  
                  if(this.fileCounter <= this.numberOfFiles){
                     this.leafCounter = 0;
                  }
               }
            }
            catch(IOException error){
               System.out.println(error);
            }          
         }

         System.out.println(curNode);
         this.nodeCounter++;

         curNode = treeQueue.poll();
      }
      System.out.println("# Total Elements : " + this.nodeCounter);
      System.out.println("# Total Leaf Elements : " + this.numberOfLeaves);
   }   
  
	private boolean isPowerTwo(int number) { return (number & (number - 1)) == 0; }

   private void checkForExit() {
      if(this.rootNode.xHigh > 0 && this.rootNode.yHigh > 0) {
         if(this.rootNode.xHigh == this.rootNode.yHigh) {
            if (this.isPowerTwo((int)this.rootNode.xHigh)) {
               if(!this.isPowerTwo((int)this.rootNode.yHigh)) {
                  System.out.println("Error : High-Y needs to be power of 2.");
                  System.exit(-1);
               }
               else{
                  this.numberOfNodes = this.getTotalNodes(this.rootNode.xHigh);
               }
            }
            else {
               System.out.println("Error : High-X needs to be power of 2.");
               System.exit(-1);
            }							
         }
         else {
            System.out.println("Error : High-X & High-Y needs to be equal.");
            System.exit(-1);
         }
     }
     else {
       System.out.println("Error : High-X and High-Y needs to be positive Number.");
       System.exit(-1);
     }
   }
  
   private String buildNodeForFile(String index, double xMin, double yMin, double xMax, double yMax){
      return index + "," + xMin + "," + yMin + "," + xMax + "," + yMax;
   }
   
   private int getTotalNodes(double maxCoordinate){
      double base = 2.0;
      double exponentH = Math.log(maxCoordinate)/Math.log(base); //log base b of n = log base e of n / log base e of b
      int totalNodes = 0;
      
      for(int i = 1; i <= exponentH; i++){
         totalNodes += Math.pow(4,i);
      }
      
      return totalNodes;
   }

   private class TreeNode {
      private double xLow, yLow, xHigh, yHigh;
      private String binaryIndex;
      private int numberIndex;

      private TreeNode(double xLow, double yLow, double xHigh, double yHigh, String binaryIndex, int numberIndex) {
         this.xLow = xLow;
         this.yLow = yLow;
         this.xHigh = xHigh;
         this.yHigh = yHigh;
         this.binaryIndex = binaryIndex;
         this.numberIndex = numberIndex;
      }
      
      @Override
      public String toString() {
         return String.format("Node : %d |$| (%.1f, %.1f, %.1f, %.1f) |$| Bin : %s\n", this.numberIndex, this.xLow, this.yLow, this.xHigh, this.yHigh, this.binaryIndex);
      }      

      private boolean isLeafNode() {
         return (this.xHigh - this.xLow) == 1 || (this.yHigh - this.yLow) == 1;
      }     
   }
}