import java.util.Date;

/**
 * Created by andrew_korneev on 21.03.2016.
 */
public class Main
{
    public static void main(String[] args)
    {
        Date tBefore = new Date();
        Body body = new Body("data.txt","3");
        Date tAfter = new Date();
        System.out.println("Calculations took " + (double) (tAfter.getTime() - tBefore.getTime()) / 1000 + " s");
    }
}
