package ui.windows;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.ui.JBIntSpinner;
import com.intellij.util.ui.FormBuilder;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import services.MiningService;
import services.RefactoringsBundle;
import services.RefactoringsMapConverter;

/**
 * Holds settings data and generates the setting panel in IntelliJ settings.
 */
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
  public SettingsComponent(Project project) {


    JButton clear = new JButton(RefactoringsBundle.message("button.clear"));
    clear.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        MiningService.getInstance(project).clear();
      }
    });
    JButton all = new JButton(RefactoringsBundle.message("button.mine"));
    all.setPreferredSize(clear.getPreferredSize());
    all.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        GitRepository repository = GitRepositoryManager
            .getInstance(project).getRepositories().get(0);
        MiningService.getInstance(project).mineAll(repository);
      }
    });
    JButton choose = new JButton(RefactoringsBundle.message("button.import"));
    choose.setPreferredSize(clear.getPreferredSize());
    choose.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        FileChooser.chooseFile(
            FileChooserDescriptorFactory.createSingleFileDescriptor("xml"),
            project,
            null,
            file -> {
              try {
                String content = VfsUtil.loadText(file);
                content = content.split("value=\"", 2)[1];
                content = content.substring(0, content.lastIndexOf('\"'));
                MiningService.getInstance(project).getState().refactoringsMap =
                    new RefactoringsMapConverter().fromString(content);
              } catch (Exception ex) {
                Messages.showErrorDialog(RefactoringsBundle.message("bad.file"),
                    RefactoringsBundle.message("name"));
              }
            }
        );
      }
    });

    myMainPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(RefactoringsBundle.message("label.max.commits"), commitLimit, 1, false)
        .addLabeledComponent(RefactoringsBundle.message("label.max.history"), historyLimit, 1,
            false)
        .addLabeledComponent(RefactoringsBundle.message("label.threads"), threads, 1, false)
        .addComponent(clear)
        .addComponent(all)
        .addComponent(choose)
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
