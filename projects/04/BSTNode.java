
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Source code example for "A Practical Introduction to Data
    Structures and Algorithm Analysis, 3rd Edition (Java)" 
    by Clifford A. Shaffer
    Copyright 2008-2011 by Clifford A. Shaffer
*/

/** Binary tree node implementation: Pointers to children
    @param E The data element
    @param Key The associated key for the record */
class BSTNode<Key, E extends SerialNode> implements BinNode<E> {
  //private Key key;              // Key for this node
  //private E element;            // Element for this node
  private MemPool memPool;
  private int key;
  private int element;
  private BSTNode<Key,E> left;  // Pointer to left child
  private BSTNode<Key,E> right; // Pointer to right child

  /** Constructors */
  public BSTNode(MemPool memPool) {this.memPool = memPool; left = right = null; }
  public BSTNode(MemPool memPool, int k, int val)
  { 
      this.memPool = memPool;
      left = right = null; 
      key = k; 
      element = val; 
  }
  
  public BSTNode(MemPool memPool, int k, int val,
                 BSTNode<Key,E> l, BSTNode<Key,E> r)
  { 
      this.memPool = memPool;
      left = l; right = r; 
      key = k; 
      element = val; 
  }

  /** Get and set the key value */
  public Key key() throws FileNotFoundException, IOException { 
      byte[] temp = new byte[256];
      int tempSize = memPool.get(temp, key, 256);
      String k = new String(Arrays.copyOf(temp, tempSize));
      return (Key) k; 
  }
  public int keyPtr() {
      return key;
  }
  public void setKey(int k) { 
      key = k; 
  }

  /** Get and set the element value */
    @Override
  public E element() {
        try {
            SerialNode sn = CityNode.create(memPool, element);
            return (E) sn;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BSTNode.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(BSTNode.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
  public int elementPtr() {
      return element;
  }
    @Override
  public void setElement(int v) { 
        element = v; 
    }

  /** Get and set the left child */
    @Override
  public BSTNode<Key,E> left() { return left; }
  public void setLeft(BSTNode<Key,E> p) { left = p; }

  /** Get and set the right child */
    @Override
  public BSTNode<Key,E> right() { return right; }
  public void setRight(BSTNode<Key,E> p) { right = p; }

  /** @return True if a leaf node, false otherwise */
    @Override
  public boolean isLeaf()
  { return (left == null) && (right == null); }
}
