package com.seeease.flywheel.web.common.context;

import java.text.NumberFormat;

/**
 * @author Tiro
 * @date 2023/2/17
 */
public class NiceStopWatch extends org.springframework.util.StopWatch {

    public NiceStopWatch(String id) {
        super(id);
    }


    public String tips() {
        return "StopWatch '" + getId() + "': running time = " + getTotalTimeMillis() + " ms";
    }

    @Override
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder(tips());
        sb.append('\n');
        sb.append("---------------------------------------------\n");
        sb.append("ms         %     Task name\n");
        sb.append("---------------------------------------------\n");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(7);
        nf.setGroupingUsed(false);
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (TaskInfo task : getTaskInfo()) {
            sb.append(nf.format(task.getTimeMillis())).append("  ");
            sb.append(pf.format((double) task.getTimeNanos() / getTotalTimeNanos())).append("  ");
            sb.append(task.getTaskName()).append("\n");
        }

        return sb.toString();
    }
}
