package swt.custom;

import org.eclipse.swt.graphics.Image;

public interface LoadingAnimationListener {
	abstract void startAnimation(Image image);
	abstract void loadingAnimation(Image image);
	abstract void stopAnimation(Image image);
}