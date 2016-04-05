import java.io.FileWriter;

/**
 * Created by andrew_korneev on 22.03.2016.
 */
public class ThreadFramework extends Body implements Runnable
{
    ThreadFramework[] threadList = new ThreadFramework[nThreads];
    volatile int end = n/nThreads;
    volatile int begin =  0;
    volatile int currentThread = 0;

    FileWriter writer;

    ThreadFramework()
    {
        init();
        System.out.println("Number of threads is: " + nThreads);

        for(int i = 0; i < threadList.length;i++)
        {
            threadList[i] = this;
            Thread t = new Thread(threadList[i],"thread" + i);
            t.setPriority(10);
            t.start();
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){/*NOP*/}
        }

        if(currentThread == nThreads)
        {
            System.out.println("...saving to file...");
            save(filename);
        }
    }

    // u[i][k+1] - u[i][k] = dt * D[i][k] / (h*h) * ( u[i+1][k+1] - 2 * u[i][k+1] + u[i-1][k+1] )
    // D[i][k] * dt / (h*h) * u[i+1][k+1] - ( 1 + 2*D[i][k] * dt / (h*h) ) * u[i][k+1]  + D[i][k] * dt / (h*h) * u[i-1][k+1] = -u[i][k]
    public void run()
    {
        // считаем на nThreads нитей, у каждой своя часть u

            for (int k = begin; k < end; k++)
            for (int i = m-2; i >=0; i--)
            {
                F[m-2 - i] = -u[m-2 - i][k];
                a[m - 1 - i] = -B[m-2-i] / (A[m-2-i] * a[m-2-i] + C[m-2-i]);
                b[m-1 - i] = (F[m-2-i] - A[m-2-i] * b[m-2-i]) / (A[m-2-i] * a[m-2-i] + C[m-2-i]);

                u[m - 1][k+1] = (F[m - 1] - A[m - 1] * b[m - 1]) / (C[m - 1] + A[m - 1] * a[m - 1]);
                u[i][k+1] = a[i+1] * u[i+1][k+1] + b[i+1];
            }

        synchronized (this)
        {
            System.out.print(begin + " : ");
            System.out.println(end);

            begin += n / nThreads;
            end += n / nThreads;
            if(end > n-1){end = n-1;}
            currentThread++;
        }
        System.out.println(Thread.currentThread().getName() + " is dead");
    }


    /*
    void save(String filename)
    {
        try
        {
            writer = new FileWriter(filename);

            writer.write("x"+ "      ");
            for(int i = 0 ; i < m; i++)
                writer.write(String.format("%7.2f",x[i]) + " ");

            writer.write("\r\n");

            for(int k = 0; k < n; k++)
            {
                writer.write("t=" + String.format("%4.3f",t[k]));
                for (int i = 0; i < m; i++)
                {
                    writer.write(String.format("%7.4f", u[i][k]) + " ");
                }
                writer.write("\r\n");
            }
            writer.flush();
            writer.close();
            System.out.println("Saved to " + filename + ".txt");
        }
        catch (IOException e){
            System.out.println("Can't save to file");
        }
    }
    */
}
