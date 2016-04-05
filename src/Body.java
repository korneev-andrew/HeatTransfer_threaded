import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by andrew_korneev on 21.03.2016.
 */
public class Body
{
    int m = 21; // количество шагов по x
    int n = 2001; // количество шагов по t
    int L = 1; // максимальная координата
    int T = 1; // максимальное время
    // int i - пространственный индекс
    // int k - временной индекс
    double dt = (double)T / (double)(n - 1); // временной шаг
    double h = (double)L / (double)(m - 1); // пространственный шаг
    double[] x = new double[m];
    double[] t = new double[n];
    volatile double[][] u = new double[m][n];
    double D; // коэфициент теплопроводности
    double f [][] = new double[m][n];
    double A[] = new double[m];
    double B[] = new double[m];
    double C[] = new double[m];
    volatile double F[] = new double[m];
    volatile double a[] = new double[m];
    volatile double b[] = new double[m];

    FileWriter writer;
    static String filename;

    static int nThreads;


    Body(){}

    Body(String filename, String nThreads)
    {
        this.filename = filename;
        init();

        if(nThreads==null||nThreads.equals("")){
        setNThreads(Runtime.getRuntime().availableProcessors());
        }
        else if(Integer.parseInt(nThreads)>0){
            setNThreads(Integer.parseInt(nThreads));
        }
        else System.out.println("Нитей должно быть ровно или больше 1 ");

        //calculate();
        new ThreadFramework();
        //save(filename);
    }

    void init() {

        for (int k = 0; k < n; k++) {
            u[0][k] = 0.0d;
            u[m - 1][k] = 0.0d;
            t[k] = k * dt;
        }

        for (int i = 0; i < m; i++) {
            x[i] = i * h;
        }

        for (int i = m / 2 - 1; i <= m / 2 + 1; i++) {
            u[i][0] = 1.0d; // температура при t=0 задается ступенькой посередине L, для читабельности в выходном файле должно быть двузначным
        }

        D = 0.01d; // устанавливаем коэфициент теплопроводности, должен быть меньше 1, иначе стержень будет нагреваться сам по себе

        double c = (D * dt) / (h * h);

        for (int i = 0; i < m; i++)
        {
            A[i] = c;
            C[i] = -1.0d - 2 * c;
            B[i] = c;
        }


        for (int i = 0; i < m - 1; i++)
        {
            F[i] = -u[i][0];
            a[i + 1] = -B[i] / (A[i] * a[i] + C[i]);
            b[i + 1] = (F[i] - A[i] * b[i]) / (A[i] * a[i] + C[i]);
        }

    }


    void calculate()
    {
        for(int k = 0; k < n-1; k++)
        for(int i = m-2; i >= 0; i--)
            {
                F[m-2 - i] = -u[m-2 - i][k];
                a[m - 1 - i] = -B[m-2-i] / (A[m-2-i] * a[m-2-i] + C[m-2-i]);
                b[m-1 - i] = (F[m-2-i] - A[m-2-i] * b[m-2-i]) / (A[m-2-i] * a[m-2-i] + C[m-2-i]);

                u[m - 1][k+1] = (F[m - 1] - A[m - 1] * b[m - 1]) / (C[m - 1] + A[m - 1] * a[m - 1]);
                u[i][k+1] = a[i+1] * u[i+1][k+1] + b[i+1];
            }

    }

    void setNThreads(int nThreads)
    {
        this.nThreads = nThreads;
    }

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
}
