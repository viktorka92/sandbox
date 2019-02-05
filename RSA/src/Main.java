import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class Main {

    public static BigInteger randbigint(BigInteger from,BigInteger to){

        BigInteger bigInteger = to;// uper limit
        BigInteger min = from;// lower limit
        BigInteger bigInteger1 = bigInteger.subtract(min);
        Random rnd = new Random();
        int maxNumBitLength = bigInteger.bitLength();

        BigInteger aRandomBigInt;

        aRandomBigInt = new BigInteger(maxNumBitLength, rnd);
        if (aRandomBigInt.compareTo(min) < 0)
            aRandomBigInt = aRandomBigInt.add(min);
        if (aRandomBigInt.compareTo(bigInteger) >= 0)
            aRandomBigInt = aRandomBigInt.mod(bigInteger1).add(min);

        return aRandomBigInt;
    }

    public static BigInteger[] KEA(BigInteger F, BigInteger e) {

        BigInteger r[] = {F, e};
        BigInteger q = BigInteger.ZERO;
        BigInteger x[] = {BigInteger.ONE, BigInteger.ZERO};
        BigInteger y[] = {BigInteger.ZERO, BigInteger.ONE};
        BigInteger tmp;
        long counter = 0;
        while (r[1].compareTo(BigInteger.ZERO)==1) {

            if (r[1].compareTo(BigInteger.ZERO)==1)
                q = r[0].divide(r[1]);

            tmp = r[1];
            r[1] = r[0].mod(r[1]);
            r[0] = tmp;

            tmp = x[1];
            x[1] = q.multiply(x[1]).add(x[0]);
            x[0] = tmp;

            tmp = y[1];
            y[1] = q.multiply(y[1]).add(y[0]);
            y[0] = tmp;
            counter++;
        }

        if (counter % 2 != 0)
            x[0] = x[0].multiply(new BigInteger("-1"));
        else
            y[0] = y[0].multiply(new BigInteger("-1"));

        //System.out.println("("+F+","+e+")"+" = "+r[0]+" = "+F+" * "+x[0]+" + "+e+" * "+y[0]);

        BigInteger[] result = {x[0], y[0]};

        return result;
    }

    public static BigInteger Gyorshatvany(BigInteger m, BigInteger e, BigInteger n) {

        String bin = "";
        while (e.compareTo(BigInteger.ZERO)==1) {
            if (e.remainder(new BigInteger("2")) == BigInteger.ZERO)
                bin += 0;
            else
                bin += 1;

            e = e.divide(new BigInteger("2"));
        }

        BigInteger[] hatvanyok = new BigInteger[bin.length()];
        hatvanyok[0] = m;

        for (int i = 1; i < bin.length(); i++) {
            hatvanyok[i] = hatvanyok[i - 1].multiply(hatvanyok[i - 1]).mod(n);
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '1') {
                result = result.multiply(hatvanyok[i]).mod(n);

            }
        }
        return result;
    }

    public static Boolean Miller_Rabin(BigInteger n) {


        BigDecimal m = new BigDecimal(n);
        int s;
        for (s = 1; ((m.subtract(BigDecimal.ONE)).divide(new BigDecimal("2").pow(s))).remainder(BigDecimal.ONE).equals(BigDecimal.ZERO); s++);
        s--;

        BigInteger d = (n.subtract(BigInteger.ONE)).divide(new BigInteger("2").pow(s));


        BigInteger[] a = new BigInteger[10];
        int x = 0;
        for (BigInteger j = new BigInteger("2"); j.compareTo(n.subtract(BigInteger.ONE))==-1 && x < 10; j=j.add(BigInteger.ONE)) {
            if (n.gcd(j).equals( BigInteger.ONE)){
                a[x] = j;
                x++;
            }
        }

        Boolean result = false;
        for (int i = 0; i < 10; i++) {
            int r;
            BigInteger c = a[i];
            for (r = 0; r < s; r++) {

                if(r==0)
                    c=Gyorshatvany(c,d,n);
                else
                    c=Gyorshatvany(c,new BigInteger("2"),n);

                if (c.equals(n.subtract(BigInteger.ONE)))
                    c = c.subtract(n);

                if (r==0 && c.compareTo(BigInteger.ONE)==0 || c.compareTo(new BigInteger("-1"))==0) {
                    result = true;
                    break;
                }
                else{
                    if (c.compareTo(new BigInteger("-1"))==0) {
                        result = true;
                        break;
                    }
                    if (c.compareTo(BigInteger.ONE)==0) {
                        break;
                    }
                }
            }
            if (r < 10)
                break;
        }
        return result;
    }

    public static BigInteger KMT(BigInteger p, BigInteger q, BigInteger c, BigInteger d, BigInteger n) {

        BigInteger max = p, min = q;
        if (q.compareTo(p)==-1) {
            max = q;
            min = p;
        }
        BigInteger mp = Gyorshatvany(c, (d.mod(p.subtract(BigInteger.ONE))), p);
        BigInteger mq = Gyorshatvany(c, (d.mod(q.subtract(BigInteger.ONE))), q);

        BigInteger result;
        if(max.equals(p))
            result = ((mp.multiply(KEA(max, min)[1]).multiply(q)).add((mq.multiply(KEA(max, min)[0]).multiply(p))));
        else
            result = ((mp.multiply(KEA(max, min)[0]).multiply(q)).add((mq.multiply(KEA(max, min)[1]).multiply(p))));


        return result.mod(n);

    }
    public static void main(String[] args) {

        BigInteger m = new BigInteger("7618646846464844684664626847974919162565");
        BigInteger q=BigInteger.ZERO;
        BigInteger p=BigInteger.ZERO;
        BigInteger n;
        BigInteger Fi;
        BigInteger e=BigInteger.ZERO;
        BigInteger d;
        BigInteger c;

        while (p.compareTo(BigInteger.ZERO)==0) {
            Random rand = new Random();
            BigInteger t = new BigInteger(2048,rand);

            if (Miller_Rabin(t) == true)
                p = t;
        }

        while (q.compareTo(BigInteger.ZERO)==0) {

            Random rand = new Random();
            BigInteger t = new BigInteger(2048,rand);

            if (Miller_Rabin(t) == true && t!=p)
                q = t;
        }

        n = p.multiply(q);
        Fi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        System.out.println("p= "+p);
        System.out.println("q= "+q);
        System.out.println("n= "+n);
        System.out.println("Fi= "+Fi);

        while (e.compareTo(BigInteger.ZERO)== 0) {
            BigInteger t = randbigint(new BigInteger("2"),Fi);
            if (t.gcd(Fi).equals(BigInteger.ONE))
                e = t;
        }

        System.out.println("e= "+e);

        d = KEA(Fi,e)[1];
        if(d.compareTo(BigInteger.ZERO)==-1)
            d=d.add(Fi);


        System.out.println("d= "+d);

        c = Gyorshatvany(m,e,n);
        System.out.println("c= "+c);

        System.out.println("m= "+Gyorshatvany(c,d,n));

        System.out.println("m= "+KMT(p,q,c,d,n));

    }
}