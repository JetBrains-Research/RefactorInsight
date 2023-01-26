package org.jetbrains.research.refactorinsight;

import com.intellij.AbstractBundle;
import com.intellij.reference.SoftReference;
import java.lang.ref.Reference;
import java.util.ResourceBundle;
import org.jetbrains.annotations.PropertyKey;

public final class RefactorInsightBundle {
  private static final String BUNDLE = "RefactorInsightBundle";
  private static Reference<ResourceBundle> INSTANCE;

  private RefactorInsightBundle() {
  }

  public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
    return AbstractBundle.message(getBundle(), key, params);
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = SoftReference.dereference(INSTANCE);
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(BUNDLE);
      INSTANCE = new SoftReference<>(bundle);
    }
    return bundle;
  }
}