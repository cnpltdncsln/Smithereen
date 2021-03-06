package smithereen.activitypub.objects;

import org.json.JSONObject;

import smithereen.activitypub.ContextCollector;

public class Image extends Document{

	public int width;
	public int height;

	@Override
	public String getType(){
		return "Image";
	}

	@Override
	public JSONObject asActivityPubObject(JSONObject obj, ContextCollector contextCollector){
		obj=super.asActivityPubObject(obj, contextCollector);
		if(width>0)
			obj.put("width", width);
		if(height>0)
			obj.put("height", height);
		return obj;
	}

	@Override
	protected ActivityPubObject parseActivityPubObject(JSONObject obj) throws Exception{
		super.parseActivityPubObject(obj);
		width=obj.optInt("width");
		height=obj.optInt("height");
		return this;
	}
}
