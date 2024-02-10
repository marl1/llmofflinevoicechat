package fr.lovc.textgen.model.input;

public class KoboldAiGenBody {
	private int max_context_length = 2048;
	private int max_length = 80;
	private  String prompt = "";
	private boolean quiet = false;
	private double rep_pen = 1.1;
	private int rep_pen_range = 256;
	private int rep_pen_slope = 1;
	private double temperature = 0.5;
	private int tfs = 1;
	private int top_a = 0;
	private int top_k = 100;
	private double top_p = 0.9;
	private int typical = 1;
	
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public int getMax_context_length() {
		return max_context_length;
	}

	public void setMax_context_length(int max_context_length) {
		this.max_context_length = max_context_length;
	}

	public int getMax_length() {
		return max_length;
	}

	public void setMax_length(int max_length) {
		this.max_length = max_length;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public double getRep_pen() {
		return rep_pen;
	}

	public void setRep_pen(double rep_pen) {
		this.rep_pen = rep_pen;
	}

	public int getRep_pen_range() {
		return rep_pen_range;
	}

	public void setRep_pen_range(int rep_pen_range) {
		this.rep_pen_range = rep_pen_range;
	}

	public int getRep_pen_slope() {
		return rep_pen_slope;
	}

	public void setRep_pen_slope(int rep_pen_slope) {
		this.rep_pen_slope = rep_pen_slope;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getTfs() {
		return tfs;
	}

	public void setTfs(int tfs) {
		this.tfs = tfs;
	}

	public int getTop_a() {
		return top_a;
	}

	public void setTop_a(int top_a) {
		this.top_a = top_a;
	}

	public int getTop_k() {
		return top_k;
	}

	public void setTop_k(int top_k) {
		this.top_k = top_k;
	}

	public double getTop_p() {
		return top_p;
	}

	public void setTop_p(double top_p) {
		this.top_p = top_p;
	}

	public int getTypical() {
		return typical;
	}

	public void setTypical(int typical) {
		this.typical = typical;
	}

	public String getPrompt() {
		return prompt;
	}
	
	
	
}
