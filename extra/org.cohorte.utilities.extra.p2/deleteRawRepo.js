{
	"name": "deleteRawRepo",
	"type": "groovy",
	"content": "repository.getRepositoryManager().delete(String.valueOf(args)); log.info('Script deleteRawRepo completed successfully! '+String.valueOf(args)+' repository deleted.')"
}