package services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.JBIntSpinner;
import com.intellij.util.ui.FormBuilder;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class SettingsComponent {
  private final JPanel myMainPanel;
  private final JBIntSpinner commitLimit =
      new JBIntSpinner(100, 0, Integer.MAX_VALUE, 10);
  private final JBIntSpinner historyLimit =
      new JBIntSpinner(100, 0, Integer.MAX_VALUE, 10);
  private final JBIntSpinner threads =
      new JBIntSpinner(8, 0, Integer.MAX_VALUE, 1);

  /**
   * SettingsComponent constructor. Creates the setting panel.
   */
  public SettingsComponent() {

    JButton clear = new JButton("Clear Cache");
    clear.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
          MiningService.getInstance(project).clear();
        }
      }
    });
    JButton all = new JButton("Mine All");
    all.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
          GitRepository repository = GitRepositoryManager
              .getInstance(project).getRepositories().get(0);
          MiningService.getInstance(project).mineAll(repository);
        }
      }
    });
    myMainPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent("Max commits to mine: ", commitLimit, 1, false)
        .addLabeledComponent("Max commits to compute history for: ", historyLimit, 1, false)
        .addLabeledComponent("Number of threads to use for mining: ", threads, 1, false)
        .addComponent(clear)
        .addComponent(all)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();

  }

  public JPanel getPanel() {
    return myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return commitLimit;
  }

  public int getCommitLimit() {
    return commitLimit.getNumber();
  }

  public void setCommitLimit(int limit) {
    this.commitLimit.setNumber(limit);
  }

  public int getHistoryLimit() {
    return historyLimit.getNumber();
  }

  public void setHistoryLimit(int limit) {
    this.historyLimit.setNumber(limit);
  }

  public int getThreads() {
    return threads.getNumber();
  }

  public void setThreads(int n) {
    this.threads.setNumber(n);
  }
}
