package smithereen.activitypub.objects;

import org.json.JSONObject;

import java.util.List;

import smithereen.activitypub.ContextCollector;

public abstract class Activity extends ActivityPubObject{

	public LinkOrObject actor;
	public LinkOrObject object;
	public LinkOrObject target;
	public List<LinkOrObject> result;
	public LinkOrObject origin;
	public LinkOrObject instrument;

	public abstract String getType();

	@Override
	public JSONObject asActivityPubObject(JSONObject obj, ContextCollector contextCollector){
		obj=super.asActivityPubObject(obj, contextCollector);
		obj.put("actor", actor.serialize(contextCollector));
		obj.put("object", object.serialize(contextCollector));
		if(target!=null)
			obj.put("target", target.serialize(contextCollector));
		if(result!=null && !result.isEmpty())
			obj.put("result", serializeLinkOrObjectArray(result, contextCollector));
		if(origin!=null)
			obj.put("origin", origin.serialize(contextCollector));
		if(instrument!=null)
			obj.put("instrument", instrument.serialize(contextCollector));
		return obj;
	}

	@Override
	protected ActivityPubObject parseActivityPubObject(JSONObject obj) throws Exception{
		super.parseActivityPubObject(obj);
		actor=tryParseLinkOrObject(obj.get("actor"));
		object=tryParseLinkOrObject(obj.get("object"));
		target=tryParseLinkOrObject(obj.opt("target"));
		result=tryParseArrayOfLinksOrObjects(obj.opt("result"));
		origin=tryParseLinkOrObject(obj.opt("origin"));
		instrument=tryParseLinkOrObject(obj.opt("instrument"));
		return this;
	}

	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(getType());
		sb.append('{');
		sb.append(super.toString());
		if(actor!=null){
			sb.append("actor=");
			sb.append(actor);
		}
		if(object!=null){
			sb.append(", object=");
			sb.append(object);
		}
		if(target!=null){
			sb.append(", target=");
			sb.append(target);
		}
		if(result!=null){
			sb.append(", result=");
			sb.append(result);
		}
		if(origin!=null){
			sb.append(", origin=");
			sb.append(origin);
		}
		if(instrument!=null){
			sb.append(", instrument=");
			sb.append(instrument);
		}
		sb.append('}');
		return sb.toString();
	}
}
