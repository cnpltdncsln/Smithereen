package smithereen.storage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import smithereen.Config;
import smithereen.activitypub.objects.ActivityPubObject;
import smithereen.activitypub.objects.Document;
import smithereen.activitypub.objects.LocalImage;
import smithereen.data.PhotoSize;
import smithereen.libvips.VImage;

public class MediaStorageUtils{

	public static long writeResizedImages(VImage img, int[] dimensions, PhotoSize.Type[] sizes, int jpegQuality, int webpQuality, String keyHex, File basePath, String baseURLPath, List<PhotoSize> outSizes) throws IOException{
		long totalSize=0;
		for(int i=0;i<sizes.length;i++){
			String baseName=keyHex+"_"+sizes[i].suffix();
			File jpeg=new File(basePath, baseName+".jpg");
			File webp=new File(basePath, baseName+".webp");
			double factor=(double)dimensions[i]/(double)Math.max(img.getWidth(), img.getHeight());
			boolean skipBiggerSizes=false;
			int width, height;
			if(factor>=1.0){
				img.writeToFile(new String[]{
						jpeg.getAbsolutePath()+"[Q="+jpegQuality+",strip=true]",
						webp.getAbsolutePath()+"[Q="+webpQuality+",strip=true]"
				});
				width=img.getWidth();
				height=img.getHeight();
				skipBiggerSizes=true;
			}else{
				VImage resized=img.resize(factor);
				try{
					resized.writeToFile(new String[]{
							jpeg.getAbsolutePath()+"[Q="+jpegQuality+",strip=true]",
							webp.getAbsolutePath()+"[Q="+webpQuality+",strip=true]"
					});
				}catch(IOException x){
					resized.release();
					throw new IOException(x);
				}
				width=resized.getWidth();
				height=resized.getHeight();
				resized.release();
			}
			outSizes.add(new PhotoSize(Config.localURI(baseURLPath+"/"+baseName+".jpg"), width, height, sizes[i], PhotoSize.Format.JPEG));
			outSizes.add(new PhotoSize(Config.localURI(baseURLPath+"/"+baseName+".webp"), width, height, sizes[i], PhotoSize.Format.WEBP));
			totalSize+=jpeg.length()+webp.length();
			if(skipBiggerSizes)
				break;
		}
		return totalSize;
	}

	public static PhotoSize findBestPhotoSize(List<PhotoSize> sizes, PhotoSize.Format format, PhotoSize.Type type){
		for(PhotoSize size:sizes){
			if(size.format==format && size.type==type)
				return size;
		}
		PhotoSize.Type smaller;
		switch(type){
			case XLARGE:
				smaller=PhotoSize.Type.LARGE;
				break;
			case LARGE:
				smaller=PhotoSize.Type.MEDIUM;
				break;
			case MEDIUM:
				smaller=PhotoSize.Type.SMALL;
				break;
			case SMALL:
				smaller=PhotoSize.Type.XSMALL;
				break;
			case XSMALL:
			default:
				return null;
		}
		return findBestPhotoSize(sizes, format, smaller);
	}

	public static void deleteAttachmentFiles(List<ActivityPubObject> attachments){
		for(ActivityPubObject o:attachments){
			if(o instanceof Document)
				deleteAttachmentFiles((Document)o);
		}
	}

	public static void deleteAttachmentFiles(Document doc){
		if(doc instanceof LocalImage){
			LocalImage img=(LocalImage) doc;
			for(PhotoSize sz:img.sizes){
				File file=new File(Config.uploadPath, img.path+"/"+img.localID+"_"+sz.type.suffix()+"."+sz.format.fileExtension());
				if(file.exists())
					file.delete();
				else
					System.out.println(file.getAbsolutePath()+" does not exist");
			}
		}
	}
}
