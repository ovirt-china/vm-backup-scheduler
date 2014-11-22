%define _version 1.0
%define _release 1

Name:		engine-vm-backup
Version:	%{_version}
Release:	%{_release}%{?dist}
Summary:	Engine auto vm backup service

Group:		ovirt-engine-third-party
License:	GPL
URL:		http://www.eayun.com
Source0:	engine-vm-backup-%{_version}.tar.gz
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)

BuildRequires:	/bin/bash
Requires:	ovirt-engine >= 3.5.0
Requires:	patternfly1

%description

%prep
%setup -q


%build
mvn clean package war:war


%install
rm -rf %{buildroot}
mkdir -p %{buildroot}/usr/share/ovirt-engine/ui-plugins/
mkdir -p %{buildroot}/usr/share/engine-vm-backup/
mkdir -p %{buildroot}/etc/httpd/conf.d/
mkdir -p %{buildroot}/etc/engine-vm-backup/
mkdir -p %{buildroot}/usr/share/ovirt-engine-jboss-as/standalone/deployments/
mkdir -p %{buildroot}/usr/share/ovirt-engine-jboss-as/standalone/configuration/
mkdir -p %{buildroot}/usr/sbin/
mkdir -p %{buildroot}/etc/rc.d/init.d/
cp -r dist/UIPlugin/* %{buildroot}/usr/share/ovirt-engine/ui-plugins/
cp dist/config/z-vm-backup-scheduler-proxy.conf %{buildroot}/etc/httpd/conf.d/
cp dist/config/engine-vm-backup.xml %{buildroot}/usr/share/ovirt-engine-jboss-as/standalone/configuration
cp target/vmBackupScheduler.war %{buildroot}/usr/share/ovirt-engine-jboss-as/standalone/deployments/
cp dist/bin/vm-backup-setup %{buildroot}/usr/sbin/
cp dist/bin/vm-backup-cleanup %{buildroot}/usr/sbin/
cp dist/service/engine-vm-backup %{buildroot}/etc/rc.d/init.d/
cp dist/dbscripts/createdb.sql %{buildroot}/usr/share/engine-vm-backup/
cp dist/config/engine-vm-backup.properties %{buildroot}/etc/engine-vm-backup/

%clean
rm -rf %{buildroot}

%post
ln -s /usr/share/patternfly1/resources/ /usr/share/ovirt-engine/ui-plugins/vbsplugin-resources/patternfly


%files
%defattr(-,root,root,-)
%dir /etc/httpd/conf.d/
%dir /etc/engine-vm-backup/
%config /etc/httpd/conf.d/z-vm-backup-scheduler-proxy.conf
%attr(0755,root,root) /usr/sbin/vm-backup-setup
%attr(0755,root,root) /usr/sbin/vm-backup-cleanup
%attr(0755,root,root) /etc/rc.d/init.d/engine-vm-backup
/usr/share/ovirt-engine/ui-plugins/
/usr/share/ovirt-engine-jboss-as/standalone/deployments/
/usr/share/ovirt-engine-jboss-as/standalone/configuration/
/usr/share/engine-vm-backup/

%postun
unlink /usr/share/ovirt-engine/ui-plugins/vbsplugin-resources/patternfly


%changelog

* Fri Nov 21 2014 MaZhe <liyang.pan@eayun.com> 1.0-1
- First build

