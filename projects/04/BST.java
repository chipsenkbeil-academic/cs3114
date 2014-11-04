
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Source code example for "A Practical Introduction to Data
    Structures and Algorithm Analysis, 3rd Edition (Java)" 
    by Clifford A. Shaffer
    Copyright 2008-2011 by Clifford A. Shaffer
*/

/*
 * Modified by Robert "Chip" Senkbeil
 * rcsvt Robert "Chip" Senkbeil
 */

/** Binary Search Tree implementation for Dictionary ADT */
class BST<Key extends Comparable<? super Key>, E extends SerialNode>
         implements Dictionary<Key, E> {
  private MemPool memPool;
  private BSTNode<Key,E> root; // Root of the BST
  int nodecount;             // Number of nodes in the BST

  /** Constructor */
  BST(MemPool memPool) { this.memPool = memPool; root = null; nodecount = 0; }

  /** Reinitialize tree */
    @Override
  public void clear() { root = null; nodecount = 0; }

  /** Insert a record into the tree.
      @param k Handle to key value of the record.
      @param e Handle to the record to insert. */
    @Override
  public void insert(int k, int e) {
        try {
            root = inserthelp(root, k, e);
            nodecount++;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BST.class.getName()).log(Level.SEVERE, null, ex);
        }
  }

  /** Remove a record from the tree.
      @param k Key value of record to remove.
      @return The record removed, null if there is none. */
    @Override
  public E remove(Key k) throws FileNotFoundException, IOException {
    E temp = findhelp(root, k);   // First find it
    if (temp != null) {
      root = removehelp(root, k); // Now remove it
      nodecount--;
    }
    return temp;
  }

  /** Remove and return the root node from the dictionary.
      @return The record removed, null if tree is empty. */
    @Override
  public E removeAny() {
        try {
            if (root == null) return null;
            E temp = root.element();
            root = removehelp(root, root.key());
            nodecount--;
            return temp;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BST.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(BST.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
  }

  /** @return Record with key value k, null if none exist.
      @param k The key value to find. */
    @Override
  public E find(Key k) {
        try {
            return findhelp(root, k);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BST.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(BST.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
 }
    
  /**
   * WRITTEN BY ROBERT C. SENKBEIL
   * Returns all elements with the matching key.
   * @param k The key to look for
   * @return The list of elements matching the provided key
   */
  public List<E> findAll(Key k) throws FileNotFoundException, IOException {
      LinkedList<E> elements = new LinkedList<E>();
      findhelpall(root, k, elements);
      return elements;
  }

  /** @return The number of records in the dictionary. */
    @Override
  public int size() { return nodecount; }
  
private E findhelp(BSTNode<Key,E> rt, Key k) throws FileNotFoundException, IOException {
  if (rt == null) return null;
  //System.out.println("'" + rt.key() + "' = '" + k + "' : " + rt.key().compareTo(k));
  if (rt.key().compareTo(k) == 0 || rt.key().equals(k)) return rt.element();
  else if (rt.key().compareTo(k) > 0) return findhelp(rt.left(), k);
  else return findhelp(rt.right(), k);
}

/**
 * WRITTEN BY ROBERT C. SENKBEIL
 * Returns all elements with the matching key starting at the provided root.
 * @param rt The root node to start the search
 * @param k The key to look for
 * @param elements The list to add elements to
 */
private void findhelpall(BSTNode<Key,E> rt, Key k, List<E> elements) throws FileNotFoundException, IOException {
    if (rt == null) return;
    if (rt.key().compareTo(k) > 0) {
        findhelpall(rt.left(), k, elements);
    } else if (rt.key().compareTo(k) == 0) {
        elements.add(rt.element());
        findhelpall(rt.right(), k, elements);
    } else {
        findhelpall(rt.right(), k, elements);
    }
}

/** @return The current subtree, modified to contain
   the new item */
private BSTNode<Key,E> inserthelp(BSTNode<Key,E> rt,
                                  int k, int e) throws FileNotFoundException, IOException {
  if (rt == null) return new BSTNode<Key,E>(memPool, k, e);
  byte[] temp = new byte[256];
  int tempSize = memPool.get(temp, k, 256);
  Comparable oKey = new String(temp, 0, tempSize);
  if (((Comparable) rt.key()).compareTo(oKey) > 0)
    rt.setLeft(inserthelp(rt.left(), k, e));
  else
    rt.setRight(inserthelp(rt.right(), k, e));
  return rt;
}
/** Remove a node with key value k
    @return The tree with the node removed */
private BSTNode<Key,E> removehelp(BSTNode<Key,E> rt, Key k) throws FileNotFoundException, IOException {
  if (rt == null) return null;
  if (rt.key().compareTo(k) > 0)
    rt.setLeft(removehelp(rt.left(), k));
  else if (rt.key().compareTo(k) < 0)
    rt.setRight(removehelp(rt.right(), k));
  else { // Found it
    if (rt.left() == null) return rt.right();
    else if (rt.right() == null) return rt.left();
    else { // Two children
      BSTNode<Key,E> temp = getmin(rt.right());
      rt.setElement(temp.elementPtr());
      rt.setKey(temp.keyPtr());
      rt.setRight(deletemin(rt.right()));
    }
  }
  return rt;
}
private BSTNode<Key,E> getmin(BSTNode<Key,E> rt) {
  if (rt.left() == null) return rt;
  return getmin(rt.left());
}
private BSTNode<Key,E> deletemin(BSTNode<Key,E> rt) {
  if (rt.left() == null) return rt.right();
  rt.setLeft(deletemin(rt.left()));
  return rt;
}
  private void printhelp(BSTNode<Key,E> rt) {
    if (rt == null) return;
    printhelp(rt.left());
    printVisit(rt.element());
    printhelp(rt.right());
  }

  private StringBuffer out;

    @Override
  public String toString() {
    out = new StringBuffer(100);
    printhelp(root);
    return out.toString();
  }
  private void printVisit(E it) {
    out.append(it).append(" ");
  }

}
