package dev.gigaherz.jsonthings.util;

public final record Frakktion(int numerator, int denominator)
{
    public Frakktion
    {
        if (denominator <= 0) throw new IllegalArgumentException("Numerator must be positive");
    }

    public Frakktion makeDivisible(int other)
    {
        if (denominator % other == 0)
            return this;

        var lcm = lcm(denominator, other);

        return new Frakktion(numerator * (lcm / denominator), lcm);
    }

    public Frakktion mul(Frakktion other)
    {
        return new Frakktion(this.numerator * other.numerator, this.denominator * other.denominator);
    }

    public Frakktion mul(int other)
    {
        return new Frakktion(this.numerator * other, this.denominator);
    }

    public Frakktion negate()
    {
        return new Frakktion(-this.numerator, this.denominator);
    }

    public Frakktion add(Frakktion other)
    {
        int numerator2 = other.numerator;
        if (numerator2 == 0)
            return this;

        var numerator1 = this.numerator;
        if (numerator1 == 0)
            return other;

        return addInternal(numerator1, numerator2, this.denominator, other.denominator);
    }

    public Frakktion add(int other)
    {
        if (other == 0)
            return this;
        return new Frakktion(this.numerator + this.denominator * other, this.denominator);
    }

    public Frakktion subtract(Frakktion other)
    {
        int numerator2 = other.numerator;
        if (numerator2 == 0)
            return this;

        var numerator1 = this.numerator;
        if (numerator1 == 0)
            return other.negate();

        return addInternal(numerator1, -numerator2, this.denominator, other.denominator);
    }

    public Frakktion subtract(int other)
    {
        if (other == 0)
            return this;
        return new Frakktion(this.numerator - this.denominator * other, this.denominator);
    }

    public Frakktion simplify()
    {
        if (numerator == 0 || numerator == 1 || denominator == 1)
            return this;
        var gcd = gcd(numerator, denominator);
        if (gcd == 1)
            return this;
        return new Frakktion(numerator/gcd, denominator/gcd);
    }

    public int intPart()
    {
        return numerator/denominator;
    }

    public int intRemainder()
    {
        return numerator%denominator;
    }

    public Frakktion remainder()
    {
        return new Frakktion(numerator%denominator, denominator);
    }

    public float asFloat()
    {
        return numerator/(float)denominator;
    }

    public double asDouble()
    {
        return numerator/(double)denominator;
    }

    @Override
    public String toString()
    {
        return toString(true);
    }

    public String toString(boolean separateFraction)
    {
        if (!separateFraction)
            return numerator + "/" + denominator;
        var mod = numerator % denominator;
        if (mod == numerator)
            return numerator + "/" + denominator;
        if (mod == 0)
            return String.valueOf(numerator/denominator);
        return "(" + (numerator/denominator) + " + " + mod + "/" + denominator + ")";
    }

    private static Frakktion addInternal(int numerator1, int numerator2, int denominator1, int denominator2)
    {
        if (denominator1 == denominator2)
        {
            return new Frakktion(numerator1 + numerator2, denominator1);
        }

        var gcd = gcd(denominator1, denominator2);
        var lcm = gcdToLcm(denominator1, denominator2, gcd);

        if (denominator2 == lcm)
        {
            var thisToLCM = numerator1 * (denominator2 / gcd);
            return new Frakktion(thisToLCM + numerator2, lcm);
        }
        else if (denominator1 == lcm)
        {
            var otherToLCM = numerator2 * (denominator1 / gcd);
            return new Frakktion(otherToLCM + numerator1, lcm);
        }
        else
        {
            var thisToLCM = numerator1 * (denominator2 / gcd);
            var otherToLCM = numerator2 * (denominator1 / gcd);
            return new Frakktion(thisToLCM + otherToLCM, lcm);
        }
    }

    public static int lcm(int number1, int number2)
    {
        if (number1 == 0 || number2 == 0)
            return 0;
        int gcd = gcd(number1, number2);
        return gcdToLcm(number1, number2, gcd);
    }

    private static int gcdToLcm(int number1, int number2, int gcd)
    {
        return mulDiv(number1, number2, gcd);
    }

    public static int gcd(int number1, int number2)
    {
        number1 = Math.abs(number1);
        number2 = Math.abs(number2);
        while (number1 != 0 && number2 != 0)
        {
            int biggerValue = Math.max(number1, number2);
            int smallerValue = Math.min(number1, number2);
            number2 = smallerValue;
            number1 = biggerValue % smallerValue;
        }
        return number1 + number2;
    }

    public static int mulDiv(int num, int mul, int div)
    {
        var tmp = Math.multiplyFull(num, mul);
        return (int) (tmp / div);
    }

    public static int mulMod(int num, int mul, int div)
    {
        var tmp = Math.multiplyFull(num, mul);
        return (int) (tmp % div);
    }

    public static class Test
    {
        public static void main(String[] args)
        {
            Frakktion a = new Frakktion(4, 10);
            Frakktion b = new Frakktion(2, 3);
            Frakktion s = a.add(b);
            System.out.println(a + " + " + b + " = " + s);
            Frakktion d = a.subtract(b);
            System.out.println(a + " - " + b + " = " + d);
            System.out.println(s + " + " + d + " = " + s.add(d));
            System.out.println(s + " - " + d + " = " + s.subtract(d));
            System.out.println(a + " * " + b + " = " + a.mul(b));
            System.out.println(s + " * " + d + " = " + s.mul(d) + " = " + s.mul(d).simplify());
        }
    }
}
