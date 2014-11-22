project_name = engine-vm-backup
version = $(shell grep "%define _version" $(project_name).spec  | awk '{print $$3}')

sources:
	git archive --format=tar --prefix=$(project_name)-$(version)/ HEAD | gzip -9v > $(project_name)-$(version).tar.gz
