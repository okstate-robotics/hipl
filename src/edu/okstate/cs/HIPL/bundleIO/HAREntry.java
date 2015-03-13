/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.bundleIO;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 *
 * @author Sridhar
 */
public class HAREntry implements Writable{
      String path;
    String[] children;

    HAREntry() {}
    
    HAREntry(String path, String[] children) {
      this.path = path;
      this.children = children;
    }

    boolean isDir() {
      return children != null;      
    }

    @Override
    public void readFields(DataInput in) throws IOException {
      path = Text.readString(in);

      if (in.readBoolean()) {
        children = new String[in.readInt()];
        for(int i = 0; i < children.length; i++) {
          children[i] = Text.readString(in);
        }
      } else {
        children = null;
      }
    }

    @Override
    public void write(DataOutput out) throws IOException {
      Text.writeString(out, path);

      final boolean dir = isDir();
      out.writeBoolean(dir);
      if (dir) {
        out.writeInt(children.length);
        for(String c : children) {
          Text.writeString(out, c);
        }
      }
    }
}
