package swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class CText extends Composite {
	
	private CLabel label;
	private Text text;
	
	public CText(Composite composite, int i) {
		super(composite, i);
		create();
	}
	
	private void create(){
		
		Display display = this.getDisplay();
		this.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		label = new CLabel(this,SWT.NONE);
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		text = new Text(this,SWT.SINGLE|SWT.BORDER);
		text.setFont(new Font(display,new FontData("MS ÉSÉVÉbÉN",10,SWT.NONE)));
		text.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.addMouseListener(new MouseListener() {
			//private boolean selectAll = false;
			@Override
			public void mouseUp(MouseEvent mouseevent) {
				if(text.isDisposed())
					return;
				if(mouseevent.count == 3){
					text.selectAll();
					//selectAll = true;
				}
				else if(mouseevent.count == 1){
//					if(selectAll){
//						text.clearSelection();
//						selectAll = false;
//					}
				}
			}
			
			@Override
			public void mouseDown(MouseEvent mouseevent) {
				
			}
			
			private final char[] tokenTable = {'.','/','?','&','='};
			
			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
				if(text.isDisposed())
					return;
				
				int position = text.getCaretPosition();
				String leftText = text.getText().substring(0, position);
				String rightText = text.getText().substring(position);
				
				System.out.println("lineNumber: " + text.getCaretLineNumber());
				System.out.println("location: " + text.getCaretLocation());
				System.out.println("pos: " + text.getCaretPosition());
				//System.out.println("pos: " + position);
				System.out.println("left: " + leftText);
				System.out.println("right: " + rightText);
				
				int leftPosition = 0;
				int rightPosition = 0;
				for(char c : tokenTable){
					int index = leftText.lastIndexOf(c);
					if(index <= -1)
						continue;
					leftPosition = index - 1;
					break;
				}
				
				for(char c : tokenTable){
					int index = rightText.indexOf(c);
					if(index <= -1)
						continue;
					rightPosition = index + 1;
					break;
				}
				
				text.setSelection(leftPosition, leftText.length() + rightPosition);
			}
		});
		
	}
	
	public void addKeyListener(KeyListener listener){
		if(listener != null && ! text.isDisposed())
			text.addKeyListener(listener);
	}
	
	public void setImage(Image image){
		if(image == null || image.isDisposed())
			return;
		if(label.isDisposed())
			return;
		label.setImage(image);
	}
	
	public Image getImage(){
		if(label.isDisposed())
			return null;
		return label.getImage();
	}
	
	public CLabel getLabel(){
		return label;
	}
	
	public Composite getComposite(){
		return this;
	}
	
	public Text getText(){
		return text;
	}
	
	public void setText(String text){
		if(!this.text.isDisposed())
			this.text.setText(text);
	}
	
}
