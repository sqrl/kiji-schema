package org.kiji.schema.tools;

import java.util.List;

import org.kiji.schema.KijiURI;

public class RowCountTool extends BaseTool {

  @Override
  public String getName() {
    return "rowcount";
  }

  @Override
  public String getDescription() {
    return "Counts the number of rows in the first locality group of a Kiji table.";
  }

  @Override
  public String getCategory() {
    return "Data";
  }

  /** {@inheritDoc} */
  @Override
  public String getUsageString() {
    return
        "Usage:\n"
        + "    kiji rowcount <table-uri>\n"
        + "\n"
        + "Example:\n"
        + "    kiji rowcount kiji://.env/default/my_table";
  }

  @Override
  protected int run(List<String> nonFlagArgs) throws Exception {
    if (nonFlagArgs.isEmpty()) {
      // TODO: Send this error to a future getErrorStream()
      getPrintStream().printf("URI must be specified as an argument%n");
      return FAILURE;
    } else if (nonFlagArgs.size() > 1) {
      getPrintStream().printf("Too many arguments: %s%n", nonFlagArgs);
      return FAILURE;
    }

    final KijiURI argURI = KijiURI.newBuilder(nonFlagArgs.get(0)).build();

    if ((null == argURI.getZookeeperQuorum())
        || (null == argURI.getInstance())
        || (null == argURI.getTable())) {
      // TODO: Send this error to a future getErrorStream()
      getPrintStream().printf("Specify a cluster, instance, and "
          + "table with argument kiji://zkhost/instance/table%n");
      return FAILURE;
    }

    
    return 0;
  }
}
