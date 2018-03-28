public class Utils {
    public Utils(){}

    /**
     * Copied from this link:
     * https://stackoverflow.com/questions/21341027/find-indexof-a-byte-array-within-another-byte-array
     */
    public static int indexOf(byte[] outerArray, byte[] smallerArray) {
        for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
               if (outerArray[i+j] != smallerArray[j]) {
                   found = false;
                   break;
               }
            }
            if (found) return i;
        }
        return -1;  
    }  
}