package LBJ2.learn;

import java.io.PrintStream;
import java.util.Random;

import LBJ2.util.ExceptionlessInputStream;
import LBJ2.util.ExceptionlessOutputStream;


/**
  * This weight vector operates similarly to its parent in the class
  * hierarchy, but it halucinates (and sets) random values for weights
  * corresponding to features it has never been asked about before.  Thus, if
  * a dot product is calculated on an empty <code>RandomWeightVector</code>,
  * all the features in the feature vector will now have random weights
  * associated with them in weight vector.  If a
  * {@link #scaledAdd(int[],double[])} is performed, any features in the
  * feature vector lacking a corresponding weight in the weight vector will
  * have a random one assigned before the addition is performed.  This is
  * usually not an issue for most algorithms, since dot products are usually
  * performed before deciding how to add, which means all the weights for that
  * feature vector will already be set when the addition is performed.  Thus,
  * it will simply appear to the algorithm that this vector had independent,
  * identically distributed random values for all its dimensions when first
  * created.
  *
  * <p> The random numbers generated by this class are Gaussian with mean 0
  * and with a user-configurable standard deviation.
  *
  * @author Nick Rizzolo
 **/
public class RandomWeightVector extends SparseWeightVector
{
  /** Keeps track of how many objects of this class have been constructed. */
  private static int instanceCount = 0;
  /** Default value for {@link #stddev}. */
  protected static final double defaultStddev = 100;


  /**
    * The random numbers that are generated by this class are Gaussian with
    * mean 0 and standard deviation defined by this variable.
   **/
  protected double stddev;
  /** Remembers the instance number of this instance. */
  protected int instanceNumber;
  /** The random number generator for this instance. */
  protected Random random;


  /** Sets a default standard deviation. */
  public RandomWeightVector() { this(defaultStddev); }

  /**
    * Sets the specified standard deviation.
    *
    * @param s  The standard deviation.
   **/
  public RandomWeightVector(double s) {
    stddev = s;
    instanceNumber = instanceCount++;
    random = new Random(instanceNumber);
  }


  /**
    * Returns the double precision value for the given feature, or
    * sets a random one and returns it if one did not already exist.
    *
    * @param featureIndex  The feature index
    * @param defaultW      Unused.
    * @return The double precision value for the given feature.
   **/
  public double getWeight(int featureIndex, double defaultW) {
    while (weights.size() <= featureIndex)
      weights.add(random.nextGaussian() * stddev);
    return weights.get(featureIndex);
  }


  /**
    * Empties the weight map and resets the random number generator.  This
    * means that the same "random" values will be filled in for the weights if
    * the same calls to {@link #dot(int[],double[],double)} and
    * {@link #scaledAdd(int[],double[],double,double)} are made in the same
    * order.
   **/
  public void clear() {
    super.clear();
    random = new Random(instanceNumber);
  }


  /**
    * Outputs the contents of this vector into the specified
    * <code>PrintStream</code>.  The string representation is the same as in
    * the super class, except the <code>"Begin"</code> annotation line also
    * contains the value of {@link #stddev} in parentheses.
    *
    * @param out  The stream to write to.
   **/
  public void write(PrintStream out) {
    out.println("Begin RandomWeightVector (" + stddev + ")");
    toStringJustWeights(out);
    out.println("End RandomWeightVector");
  }


  /**
    * Outputs the contents of this vector into the specified
    * <code>PrintStream</code>.  The string representation is the same as in
    * the super class, except the <code>"Begin"</code> annotation line also
    * contains the value of {@link #stddev} in parentheses.
    *
    * @param out  The stream to write to.
    * @param lex  The feature lexicon.
   **/
  public void write(PrintStream out, Lexicon lex) {
    out.println("Begin RandomWeightVector (" + stddev + ")");
    toStringJustWeights(out, 0, lex);
    out.println("End RandomWeightVector");
  }


  /**
    * Writes the weight vector's internal representation in binary form.
    *
    * @param out  The output stream.
   **/
  public void write(ExceptionlessOutputStream out) {
    super.write(out);
    out.writeDouble(stddev);
    out.writeInt(instanceNumber);
    // Not perfect; to preserve the current semantics of this class (which are
    // also less than ideal), we should serialze the random object into the
    // stream so it can continue where it left off when read back in.
  }


  /**
    * Reads the representation of a weight vector with this object's run-time
    * type from the given stream, overwriting the data in this object.
    *
    * <p> This method is appropriate for reading weight vectors as written by
    * {@link #write(ExceptionlessOutputStream)}.
    *
    * @param in The input stream.
   **/
  public void read(ExceptionlessInputStream in) {
    super.read(in);
    stddev = in.readDouble();
    instanceNumber = in.readInt();
    random = new Random(instanceNumber);
      // Not perfect; see the comment in #write(ExceptionlessOutputStream)
  }


  /**
    * Returns a new, empty weight vector with the same parameter settings as
    * this one.
    *
    * @return An empty weight vector.
   **/
  public SparseWeightVector emptyClone() {
    return new RandomWeightVector(stddev);
  }
}
