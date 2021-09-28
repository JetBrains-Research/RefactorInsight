package org.jetbrains.research.refactorinsight.ui.windows;

import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.vcs.log.ui.table.GraphTableModel;
import com.intellij.vcs.log.ui.table.VcsLogCellRenderer;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import com.intellij.vcs.log.ui.table.column.VcsLogCustomColumn;
import icons.RefactorInsightIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.services.MiningService;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class RefactoringColumn implements VcsLogCustomColumn<Boolean> {

  @NotNull
  @Override
  public TableCellRenderer createTableCellRenderer(@NotNull VcsLogGraphTable table) {
    return new MyCellRenderer();
  }

  @Override
  public Boolean getValue(@NotNull GraphTableModel graphTableModel, int row) {
    MiningService miningService = graphTableModel.getLogData().getProject().getServiceIfCreated(MiningService.class);
    if (miningService == null) {
      return Boolean.FALSE;
    }
    String commitHash = graphTableModel.getCommitId(row).getHash().asString();
    return miningService.containsRefactoring(commitHash);
  }

  @NotNull
  @Override
  public String getId() {
    return "RefactorInsight.Refactoring";
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Nls
  @NotNull
  @Override
  public String getLocalizedName() {
    return RefactorInsightBundle.message("refactoring.column.title");
  }

  @Override
  public Boolean getStubValue(@NotNull GraphTableModel graphTableModel) {
    return Boolean.FALSE;
  }

  @Override
  public boolean isEnabledByDefault() {
    return false;
  }

  private static class MyCellRenderer extends ColoredTableCellRenderer implements VcsLogCellRenderer {
    @Override
    protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected,
                                         boolean hasFocus, int row, int column) {
      if (!(value instanceof Boolean) || !(table instanceof VcsLogGraphTable)) {
        return;
      }

      ((VcsLogGraphTable) table).applyHighlighters(this, row, column, hasFocus, selected);

      customizeCellRenderer((Boolean) value);
    }

    private void customizeCellRenderer(@NotNull Boolean value) {
      setBorder(null);
      if (value) {
        setIcon(RefactorInsightIcons.node);
        setTransparentIconBackground(true);
      }
    }

    @Override
    public @Nullable Integer getPreferredWidth(@NotNull JTable table) {
      customizeCellRenderer(true);
      return getPreferredSize().width;
    }
  }
}
