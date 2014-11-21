%define _version 1.0
%define _release 1

Name:		Engine-Vm-Backup
Version:	%{_version}
Release:	%{_release}%{?dist}
Summary:	Engine auto vm backup service

Group:		ovirt-engine-third-party
License:	GPL
URL:		http://www.eayun.com
Source0:	Engine-Vm-Backup-%{_version}.tar.gz
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
mkdir -p %{buildroot}/etc/httpd/conf.d/
mkdir -p %{buildroot}/usr/share/ovirt-engine-jboss-as/standalone/deployments/
cp -r dist/UIPlugin/* %{buildroot}/usr/share/ovirt-engine/ui-plugins/
cp dist/httpd/z-vm-backup-scheduler-proxy.conf %{buildroot}/etc/httpd/conf.d/
cp target/vmBackupScheduler.war %{buildroot}/usr/share/ovirt-engine-jboss-as/standalone/deployments/

%clean
rm -rf %{buildroot}

%post
ln -s /usr/share/patternfly1/resources/ /usr/share/ovirt-engine/ui-plugins/vbsplugin-resources/patternfly


%files
%defattr(-,root,root,-)
%dir /etc/httpd/conf.d/
%config /etc/httpd/conf.d/z-vm-backup-scheduler-proxy.conf
/usr/share/ovirt-engine/ui-plugins/
/usr/share/ovirt-engine-jboss-as/standalone/deployments/


%changelog

* Fri Nov 21 2014 MaZhe <liyang.pan@eayun.com> 1.0-1
- First build

