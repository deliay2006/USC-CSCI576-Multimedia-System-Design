
public class Cluster {
    int id;
    int averageRed;
    int totalRed;
    int pixelNum;
    
    public Cluster(int id, int pixel) {
    	this.id = id;
    	int r = pixel>>16&0x000000FF;
        averageRed = r;
        addPixel(pixel);
    }

    int getId() {
        return id;
    }

    int getPixel() {
        int r = averageRed;
        int g = r;
        int b = r;
        return 0xff000000|r<<16|g<<8|b;
    }
    
    void addPixel(int pixel) {
        int r = pixel>>16&0x000000FF;
        totalRed += r;
        pixelNum++;
        averageRed = totalRed/pixelNum;
    }
    
    void removePixel(int pixel) {
        int r = pixel>>16&0x000000FF;
        totalRed -= r;
        pixelNum--;
        averageRed = totalRed/pixelNum;
    }
    
    int distance(int pixel) {
        int r = pixel>>16&0x000000FF;
        return Math.abs(averageRed - r);
    }
}