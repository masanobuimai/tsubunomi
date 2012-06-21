package jp.katahirado.android.tsubunomi;

import java.util.Comparator

public class LowerCaseComparator() : Comparator<String>{
  public override fun equals(p0: Any?): Boolean {
    throw UnsupportedOperationException()
  }
  public override fun compare(p0: String?, p1: String?): Int {
    return p0!!.compareToIgnoreCase(p1!!)
  }
}
