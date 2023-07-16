package betterwithmods.client.container;

import betterwithmods.api.util.IProgressSource;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

public abstract class ContainerProgress extends Container {
    private IProgressSource progressSource;

    private int previousProgress, previousMax;
    private int progress, max;

    public ContainerProgress(IProgressSource progressSource) {
        this.progressSource = progressSource;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendWindowProperty(this, 0, progressSource.getMax());
        listener.sendWindowProperty(this, 1, progressSource.getProgress());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        this.progress = progressSource.getProgress();
        this.max = progressSource.getMax();

        for (IContainerListener craft : this.listeners) {

            if (this.previousMax != this.max)
                craft.sendWindowProperty(this, 0, this.max);
            if (this.previousProgress != progress)
                craft.sendWindowProperty(this, 1, progress);

        }

        this.previousProgress = this.progress;
        this.previousMax = this.max;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                this.max = data;
            case 1:
                this.progress = data;
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    public boolean showProgress() {
        return progress > 0 && max > 0;
    }

}
