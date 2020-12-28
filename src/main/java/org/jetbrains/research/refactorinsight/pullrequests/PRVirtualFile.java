package org.jetbrains.research.refactorinsight.pullrequests;

import com.intellij.ide.actions.SplitAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFilePathWrapper;
import com.intellij.openapi.vfs.VirtualFileWithoutContent;
import com.intellij.testFramework.LightVirtualFileBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PRVirtualFile extends LightVirtualFileBase implements VirtualFileWithoutContent, VirtualFilePathWrapper {
  List<String> commitsIds;

  /**
   * Creates a new virtual file.
   *
   * @param name              file name.
   * @param fileType          file type.
   * @param modificationStamp file modification stamp.
   * @param commitsIds        ids of commits from selected PR.
   */
  public PRVirtualFile(@NlsSafe String name, FileType fileType, long modificationStamp, List<String> commitsIds) {
    super(name, fileType, modificationStamp);
    this.setWritable(false);
    this.putUserDataIfAbsent(SplitAction.FORBID_TAB_SPLIT, true);
    this.commitsIds = commitsIds;
  }

  public List<String> getCommitsIds() {
    return this.commitsIds;
  }

  @Override
  public @NotNull
  OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) {
    return null;
  }

  @Override
  public @NotNull
  byte[] contentsToByteArray() {
    return "RefactorInsight".getBytes();
  }

  @Override
  public InputStream getInputStream() {
    return null;
  }

  @Override
  public @NotNull
  String getPresentablePath() {
    return RefactorInsightBundle.message("discovered.refactorings.in.pr");
  }

  @Override
  public boolean enforcePresentableName() {
    return false;
  }
}
