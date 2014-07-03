namespace CloudStack.Plugin.WmiWrappers.ROOT.MICROSOFT.WINDOWS.STORAGE {
    using System;
    using System.ComponentModel;
    using System.Management;
    using System.Collections;
    using System.Globalization;
    using System.ComponentModel.Design.Serialization;
    using System.Reflection;
    
    
    // Functions ShouldSerialize<PropertyName> are functions used by VS property browser to check if a particular property has to be serialized. These functions are added for all ValueType properties ( properties of type Int32, BOOL etc.. which cannot be set to null). These functions use Is<PropertyName>Null function. These functions are also used in the TypeConverter implementation for the properties to check for NULL value of property so that an empty value can be shown in Property browser in case of Drag and Drop in Visual studio.
    // Functions Is<PropertyName>Null() are used to check if a property is NULL.
    // Functions Reset<PropertyName> are added for Nullable Read/Write properties. These functions are used by VS designer in property browser to set a property to NULL.
    // Every property added to the class for WMI property has attributes set to define its behavior in Visual Studio designer and also to define a TypeConverter to be used.
    // An Early Bound class generated for the WMI class.MSFT_Disk
    public class Disk : System.ComponentModel.Component {
        
        // Private property to hold the WMI namespace in which the class resides.
        private static string CreatedWmiNamespace = "ROOT\\Microsoft\\Windows\\Storage";
        
        // Private property to hold the name of WMI class which created this class.
        private static string CreatedClassName = "MSFT_Disk";
        
        // Private member variable to hold the ManagementScope which is used by the various methods.
        private static System.Management.ManagementScope statMgmtScope = null;
        
        private ManagementSystemProperties PrivateSystemProperties;
        
        // Underlying lateBound WMI object.
        private System.Management.ManagementObject PrivateLateBoundObject;
        
        // Member variable to store the 'automatic commit' behavior for the class.
        private bool AutoCommitProp;
        
        // Private variable to hold the embedded property representing the instance.
        private System.Management.ManagementBaseObject embeddedObj;
        
        // The current WMI object used
        private System.Management.ManagementBaseObject curObj;
        
        // Flag to indicate if the instance is an embedded object.
        private bool isEmbedded;
        
        // Below are different overloads of constructors to initialize an instance of the class with a WMI object.
        public Disk() {
            this.InitializeObject(null, null, null);
        }
        
        public Disk(string keyObjectId) {
            this.InitializeObject(null, new System.Management.ManagementPath(Disk.ConstructPath(keyObjectId)), null);
        }
        
        public Disk(System.Management.ManagementScope mgmtScope, string keyObjectId) {
            this.InitializeObject(((System.Management.ManagementScope)(mgmtScope)), new System.Management.ManagementPath(Disk.ConstructPath(keyObjectId)), null);
        }
        
        public Disk(System.Management.ManagementPath path, System.Management.ObjectGetOptions getOptions) {
            this.InitializeObject(null, path, getOptions);
        }
        
        public Disk(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path) {
            this.InitializeObject(mgmtScope, path, null);
        }
        
        public Disk(System.Management.ManagementPath path) {
            this.InitializeObject(null, path, null);
        }
        
        public Disk(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path, System.Management.ObjectGetOptions getOptions) {
            this.InitializeObject(mgmtScope, path, getOptions);
        }
        
        public Disk(System.Management.ManagementObject theObject) {
            Initialize0();
            if ((CheckIfProperClass(theObject) == true)) {
                PrivateLateBoundObject = theObject;
                PrivateSystemProperties = new ManagementSystemProperties(PrivateLateBoundObject);
                curObj = PrivateLateBoundObject;
            }
            else {
                throw new System.ArgumentException("Class name does not match.");
            }
        }
        
        public Disk(System.Management.ManagementBaseObject theObject) {
            Initialize0();
            if ((CheckIfProperClass(theObject) == true)) {
                embeddedObj = theObject;
                PrivateSystemProperties = new ManagementSystemProperties(theObject);
                curObj = embeddedObj;
                isEmbedded = true;
            }
            else {
                throw new System.ArgumentException("Class name does not match.");
            }
        }
        
        // Property returns the namespace of the WMI class.
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public string OriginatingNamespace {
            get {
                return "ROOT\\Microsoft\\Windows\\Storage";
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public string ManagementClassName {
            get {
                string strRet = CreatedClassName;
                if ((curObj != null)) {
                    if ((curObj.ClassPath != null)) {
                        strRet = ((string)(curObj["__CLASS"]));
                        if (((strRet == null) 
                                    || (strRet == string.Empty))) {
                            strRet = CreatedClassName;
                        }
                    }
                }
                return strRet;
            }
        }
        
        // Property pointing to an embedded object to get System properties of the WMI object.
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public ManagementSystemProperties SystemProperties {
            get {
                return PrivateSystemProperties;
            }
        }
        
        // Property returning the underlying lateBound object.
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public System.Management.ManagementBaseObject LateBoundObject {
            get {
                return curObj;
            }
        }
        
        // ManagementScope of the object.
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public System.Management.ManagementScope Scope {
            get {
                if ((isEmbedded == false)) {
                    return PrivateLateBoundObject.Scope;
                }
                else {
                    return null;
                }
            }
            set {
                if ((isEmbedded == false)) {
                    PrivateLateBoundObject.Scope = value;
                }
            }
        }
        
        // Property to show the commit behavior for the WMI object. If true, WMI object will be automatically saved after each property modification.(ie. Put() is called after modification of a property).
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool AutoCommit {
            get {
                return AutoCommitProp;
            }
            set {
                AutoCommitProp = value;
            }
        }
        
        // The ManagementPath of the underlying WMI object.
        [Browsable(true)]
        public System.Management.ManagementPath Path {
            get {
                if ((isEmbedded == false)) {
                    return PrivateLateBoundObject.Path;
                }
                else {
                    return null;
                }
            }
            set {
                if ((isEmbedded == false)) {
                    if ((CheckIfProperClass(null, value, null) != true)) {
                        throw new System.ArgumentException("Class name does not match.");
                    }
                    PrivateLateBoundObject.Path = value;
                }
            }
        }
        
        // Public static scope property which is used by the various methods.
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public static System.Management.ManagementScope StaticScope {
            get {
                return statMgmtScope;
            }
            set {
                statMgmtScope = value;
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsAllocatedSizeNull {
            get {
                if ((curObj["AllocatedSize"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("The amount of space currently used on the disk.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public ulong AllocatedSize {
            get {
                if ((curObj["AllocatedSize"] == null)) {
                    return System.Convert.ToUInt64(0);
                }
                return ((ulong)(curObj["AllocatedSize"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsBootFromDiskNull {
            get {
                if ((curObj["BootFromDisk"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description(@"This property indicates that the computer is configured to start off of this disk. On computers with BIOS firmware, this is the first disk that the firmware detects during startup. On computers that use EFI firmware, this is the disk that contains the EFI System Partition (ESP). If there are no disks or multiple disks with an ESP partition, this flag is not set for any disk.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public bool BootFromDisk {
            get {
                if ((curObj["BootFromDisk"] == null)) {
                    return System.Convert.ToBoolean(0);
                }
                return ((bool)(curObj["BootFromDisk"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsBusTypeNull {
            get {
                if ((curObj["BusType"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Denotes the I/O bus type used by this disk.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public BusTypeValues BusType {
            get {
                if ((curObj["BusType"] == null)) {
                    return ((BusTypeValues)(System.Convert.ToInt32(18)));
                }
                return ((BusTypeValues)(System.Convert.ToInt32(curObj["BusType"])));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("A string representation of the disk\'s firmware version.")]
        public string FirmwareVersion {
            get {
                return ((string)(curObj["FirmwareVersion"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("FriendlyName is a user-friendly, display-oriented string to identify the disk.")]
        public string FriendlyName {
            get {
                return ((string)(curObj["FriendlyName"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("The GPT guid of the disk. This property is only valid on GPT disks and will be NU" +
            "LL for all other disk types.")]
        public string Guid {
            get {
                return ((string)(curObj["Guid"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsHealthStatusNull {
            get {
                if ((curObj["HealthStatus"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public HealthStatusValues HealthStatus {
            get {
                if ((curObj["HealthStatus"] == null)) {
                    return ((HealthStatusValues)(System.Convert.ToInt32(9)));
                }
                return ((HealthStatusValues)(System.Convert.ToInt32(curObj["HealthStatus"])));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsIsBootNull {
            get {
                if ((curObj["IsBoot"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("This property indicates that the computer has booted off of this disk.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public bool IsBoot {
            get {
                if ((curObj["IsBoot"] == null)) {
                    return System.Convert.ToBoolean(0);
                }
                return ((bool)(curObj["IsBoot"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsIsClusteredNull {
            get {
                if ((curObj["IsClustered"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("If IsClustered is TRUE, this disk is used in a clustered environment.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public bool IsClustered {
            get {
                if ((curObj["IsClustered"] == null)) {
                    return System.Convert.ToBoolean(0);
                }
                return ((bool)(curObj["IsClustered"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsIsOfflineNull {
            get {
                if ((curObj["IsOffline"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public bool IsOffline {
            get {
                if ((curObj["IsOffline"] == null)) {
                    return System.Convert.ToBoolean(0);
                }
                return ((bool)(curObj["IsOffline"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsIsReadOnlyNull {
            get {
                if ((curObj["IsReadOnly"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public bool IsReadOnly {
            get {
                if ((curObj["IsReadOnly"] == null)) {
                    return System.Convert.ToBoolean(0);
                }
                return ((bool)(curObj["IsReadOnly"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsIsSystemNull {
            get {
                if ((curObj["IsSystem"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("If IsSystem is TRUE, this disk contains the system partition.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public bool IsSystem {
            get {
                if ((curObj["IsSystem"] == null)) {
                    return System.Convert.ToBoolean(0);
                }
                return ((bool)(curObj["IsSystem"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsLargestFreeExtentNull {
            get {
                if ((curObj["LargestFreeExtent"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("This field indicates the largest contiguous block of free space on the disk. This" +
            " is also the largest size of a partition which can be created on the disk.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public ulong LargestFreeExtent {
            get {
                if ((curObj["LargestFreeExtent"] == null)) {
                    return System.Convert.ToUInt64(0);
                }
                return ((ulong)(curObj["LargestFreeExtent"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description(@"Location contains the PnP location path of the disk. The format of this string depends on the bus type. If the bus type is SCSI, SAS, or PCI RAID, the format is <AdapterPnpLocationPath>#<BusType>(P<PathId>T<TargetId>L<LunId>). If the bus type is IDE, ATA, PATA, or SATA, the format is <AdapterPnpLocationPath>#<BusType>(C<PathId>T<TargetId>L<LunId>). For example, a SCSI location may look like: PCIROOT(0)#PCI(1C00)#PCI(0000)#SCSI(P00T01L01). Note: For Hyper-V and VHD images, this member is NULL because the virtual controller does not return the location path.")]
        public string Location {
            get {
                return ((string)(curObj["Location"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsLogicalSectorSizeNull {
            get {
                if ((curObj["LogicalSectorSize"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("This field indicates the logical sector size of the disk in bytes. For example: a" +
            " 4K native disk will report 4096, while a 512 emulated disk will report 512.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public uint LogicalSectorSize {
            get {
                if ((curObj["LogicalSectorSize"] == null)) {
                    return System.Convert.ToUInt32(0);
                }
                return ((uint)(curObj["LogicalSectorSize"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("A string representation of the disk\'s hardware manufacturer.")]
        public string Manufacturer {
            get {
                return ((string)(curObj["Manufacturer"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("A string representation of the disk\'s model.")]
        public string Model {
            get {
                return ((string)(curObj["Model"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsNumberNull {
            get {
                if ((curObj["Number"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("The operating system\'s number for the disk. Disk 0 is typically the boot device. " +
            "Disk numbers may not necessarily remain the same across reboots.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public uint Number {
            get {
                if ((curObj["Number"] == null)) {
                    return System.Convert.ToUInt32(0);
                }
                return ((uint)(curObj["Number"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsNumberOfPartitionsNull {
            get {
                if ((curObj["NumberOfPartitions"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public uint NumberOfPartitions {
            get {
                if ((curObj["NumberOfPartitions"] == null)) {
                    return System.Convert.ToUInt32(0);
                }
                return ((uint)(curObj["NumberOfPartitions"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("ObjectId is a mandatory property that is used to opaquely and uniquely identify a" +
            "n instance of a class within the scope of the host computer system.")]
        public string ObjectId {
            get {
                return ((string)(curObj["ObjectId"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsOfflineReasonNull {
            get {
                if ((curObj["OfflineReason"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description(@"If IsOffline is TRUE, this property informs the user of the specific reason for the disk being offline. 
1 - 'Policy': The user requested the disk to be offline. 
2 - 'Redundant Path': The disk is used for multi-path I/O. 
3 - 'Snapshot': The disk is a snapshot disk. 
4 - 'Collision': There was a signature or identifier collision with another disk. 
5 - 'Resource Exhaustion': There were insufficient resources to bring the disk online. 
6 - 'Critical Write Failures': There were critical write failures on the disk. 
7 - 'Data Integrity Scan Required': A data integrity scan is required.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public OfflineReasonValues OfflineReason {
            get {
                if ((curObj["OfflineReason"] == null)) {
                    return ((OfflineReasonValues)(System.Convert.ToInt32(0)));
                }
                return ((OfflineReasonValues)(System.Convert.ToInt32(curObj["OfflineReason"])));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsOperationalStatusNull {
            get {
                if ((curObj["OperationalStatus"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public OperationalStatusValues OperationalStatus {
            get {
                if ((curObj["OperationalStatus"] == null)) {
                    return ((OperationalStatusValues)(System.Convert.ToInt32(7)));
                }
                return ((OperationalStatusValues)(System.Convert.ToInt32(curObj["OperationalStatus"])));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsPartitionStyleNull {
            get {
                if ((curObj["PartitionStyle"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public PartitionStyleValues PartitionStyle {
            get {
                if ((curObj["PartitionStyle"] == null)) {
                    return ((PartitionStyleValues)(System.Convert.ToInt32(3)));
                }
                return ((PartitionStyleValues)(System.Convert.ToInt32(curObj["PartitionStyle"])));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Path can be used to open an operating system handle to the disk device.")]
        public string Path0 {
            get {
                return ((string)(curObj["Path"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsPhysicalSectorSizeNull {
            get {
                if ((curObj["PhysicalSectorSize"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("This field indicates the physical sector size of the disk in bytes. For example: " +
            "both 4K native disks and 512 emulated disks will report 4096.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public uint PhysicalSectorSize {
            get {
                if ((curObj["PhysicalSectorSize"] == null)) {
                    return System.Convert.ToUInt32(0);
                }
                return ((uint)(curObj["PhysicalSectorSize"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsProvisioningTypeNull {
            get {
                if ((curObj["ProvisioningType"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Denotes the provisioning type of the disk device. \n1 - \'Thin\' means that the stor" +
            "age for the disk is allocated on-demand. \n2 - \'Fixed\' means that the storage is " +
            "allocated up front.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public ProvisioningTypeValues ProvisioningType {
            get {
                if ((curObj["ProvisioningType"] == null)) {
                    return ((ProvisioningTypeValues)(System.Convert.ToInt32(3)));
                }
                return ((ProvisioningTypeValues)(System.Convert.ToInt32(curObj["ProvisioningType"])));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("A string representation of the disk\'s serial number.")]
        public string SerialNumber {
            get {
                return ((string)(curObj["SerialNumber"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsSignatureNull {
            get {
                if ((curObj["Signature"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("The MBR signature of the disk. This property is only valid on MBR disks and will " +
            "be NULL for all other disk types.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public uint Signature {
            get {
                if ((curObj["Signature"] == null)) {
                    return System.Convert.ToUInt32(0);
                }
                return ((uint)(curObj["Signature"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsSizeNull {
            get {
                if ((curObj["Size"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("The total size of the disk, measured in bytes.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public ulong Size {
            get {
                if ((curObj["Size"] == null)) {
                    return System.Convert.ToUInt64(0);
                }
                return ((ulong)(curObj["Size"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description(@"UniqueId of a disk contains the VPD Page 0x83 information that uniquely identifies this disk. The following types are accepted (in order of precedence): 8 - SCSI Name String; 3 - FCPH Name; 2 - EUI64, 1 - Vendor Id, 0 - Vendor Specific. If the disk is an exposed VirtualDisk, UniqueId is used map the association between the two objects.")]
        public string UniqueId {
            get {
                return ((string)(curObj["UniqueId"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsUniqueIdFormatNull {
            get {
                if ((curObj["UniqueIdFormat"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("UniqueIdFormat informs the user what VPD Page 0x83 descriptor type was used to po" +
            "pulate the UniqueId field.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public UniqueIdFormatValues UniqueIdFormat {
            get {
                if ((curObj["UniqueIdFormat"] == null)) {
                    return ((UniqueIdFormatValues)(System.Convert.ToInt32(9)));
                }
                return ((UniqueIdFormatValues)(System.Convert.ToInt32(curObj["UniqueIdFormat"])));
            }
        }
        
        private bool CheckIfProperClass(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path, System.Management.ObjectGetOptions OptionsParam) {
            if (((path != null) 
                        && (string.Compare(path.ClassName, this.ManagementClassName, true, System.Globalization.CultureInfo.InvariantCulture) == 0))) {
                return true;
            }
            else {
                return CheckIfProperClass(new System.Management.ManagementObject(mgmtScope, path, OptionsParam));
            }
        }
        
        private bool CheckIfProperClass(System.Management.ManagementBaseObject theObj) {
            if (((theObj != null) 
                        && (string.Compare(((string)(theObj["__CLASS"])), this.ManagementClassName, true, System.Globalization.CultureInfo.InvariantCulture) == 0))) {
                return true;
            }
            else {
                System.Array parentClasses = ((System.Array)(theObj["__DERIVATION"]));
                if ((parentClasses != null)) {
                    int count = 0;
                    for (count = 0; (count < parentClasses.Length); count = (count + 1)) {
                        if ((string.Compare(((string)(parentClasses.GetValue(count))), this.ManagementClassName, true, System.Globalization.CultureInfo.InvariantCulture) == 0)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        
        private bool ShouldSerializeAllocatedSize() {
            if ((this.IsAllocatedSizeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeBootFromDisk() {
            if ((this.IsBootFromDiskNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeBusType() {
            if ((this.IsBusTypeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeHealthStatus() {
            if ((this.IsHealthStatusNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeIsBoot() {
            if ((this.IsIsBootNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeIsClustered() {
            if ((this.IsIsClusteredNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeIsOffline() {
            if ((this.IsIsOfflineNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeIsReadOnly() {
            if ((this.IsIsReadOnlyNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeIsSystem() {
            if ((this.IsIsSystemNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeLargestFreeExtent() {
            if ((this.IsLargestFreeExtentNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeLogicalSectorSize() {
            if ((this.IsLogicalSectorSizeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeNumber() {
            if ((this.IsNumberNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeNumberOfPartitions() {
            if ((this.IsNumberOfPartitionsNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeOfflineReason() {
            if ((this.IsOfflineReasonNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeOperationalStatus() {
            if ((this.IsOperationalStatusNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializePartitionStyle() {
            if ((this.IsPartitionStyleNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializePhysicalSectorSize() {
            if ((this.IsPhysicalSectorSizeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeProvisioningType() {
            if ((this.IsProvisioningTypeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeSignature() {
            if ((this.IsSignatureNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeSize() {
            if ((this.IsSizeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeUniqueIdFormat() {
            if ((this.IsUniqueIdFormatNull == false)) {
                return true;
            }
            return false;
        }
        
        [Browsable(true)]
        public void CommitObject() {
            if ((isEmbedded == false)) {
                PrivateLateBoundObject.Put();
            }
        }
        
        [Browsable(true)]
        public void CommitObject(System.Management.PutOptions putOptions) {
            if ((isEmbedded == false)) {
                PrivateLateBoundObject.Put(putOptions);
            }
        }
        
        private void Initialize0() {
            AutoCommitProp = true;
            isEmbedded = false;
        }
        
        private static string ConstructPath(string keyObjectId) {
            string strPath = "ROOT\\Microsoft\\Windows\\Storage:MSFT_Disk";
            strPath = string.Concat(strPath, string.Concat(".ObjectId=", string.Concat("\"", string.Concat(keyObjectId, "\""))));
            return strPath;
        }
        
        private void InitializeObject(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path, System.Management.ObjectGetOptions getOptions) {
            Initialize0();
            if ((path != null)) {
                if ((CheckIfProperClass(mgmtScope, path, getOptions) != true)) {
                    throw new System.ArgumentException("Class name does not match.");
                }
            }
            PrivateLateBoundObject = new System.Management.ManagementObject(mgmtScope, path, getOptions);
            PrivateSystemProperties = new ManagementSystemProperties(PrivateLateBoundObject);
            curObj = PrivateLateBoundObject;
        }
        
        // Different overloads of GetInstances() help in enumerating instances of the WMI class.
        public static DiskCollection GetInstances() {
            return GetInstances(null, null, null);
        }
        
        public static DiskCollection GetInstances(string condition) {
            return GetInstances(null, condition, null);
        }
        
        public static DiskCollection GetInstances(string[] selectedProperties) {
            return GetInstances(null, null, selectedProperties);
        }
        
        public static DiskCollection GetInstances(string condition, string[] selectedProperties) {
            return GetInstances(null, condition, selectedProperties);
        }
        
        public static DiskCollection GetInstances(System.Management.ManagementScope mgmtScope, System.Management.EnumerationOptions enumOptions) {
            if ((mgmtScope == null)) {
                if ((statMgmtScope == null)) {
                    mgmtScope = new System.Management.ManagementScope();
                    mgmtScope.Path.NamespacePath = "root\\Microsoft\\Windows\\Storage";
                }
                else {
                    mgmtScope = statMgmtScope;
                }
            }
            System.Management.ManagementPath pathObj = new System.Management.ManagementPath();
            pathObj.ClassName = "MSFT_Disk";
            pathObj.NamespacePath = "root\\Microsoft\\Windows\\Storage";
            System.Management.ManagementClass clsObject = new System.Management.ManagementClass(mgmtScope, pathObj, null);
            if ((enumOptions == null)) {
                enumOptions = new System.Management.EnumerationOptions();
                enumOptions.EnsureLocatable = true;
            }
            return new DiskCollection(clsObject.GetInstances(enumOptions));
        }
        
        public static DiskCollection GetInstances(System.Management.ManagementScope mgmtScope, string condition) {
            return GetInstances(mgmtScope, condition, null);
        }
        
        public static DiskCollection GetInstances(System.Management.ManagementScope mgmtScope, string[] selectedProperties) {
            return GetInstances(mgmtScope, null, selectedProperties);
        }
        
        public static DiskCollection GetInstances(System.Management.ManagementScope mgmtScope, string condition, string[] selectedProperties) {
            if ((mgmtScope == null)) {
                if ((statMgmtScope == null)) {
                    mgmtScope = new System.Management.ManagementScope();
                    mgmtScope.Path.NamespacePath = "root\\Microsoft\\Windows\\Storage";
                }
                else {
                    mgmtScope = statMgmtScope;
                }
            }
            System.Management.ManagementObjectSearcher ObjectSearcher = new System.Management.ManagementObjectSearcher(mgmtScope, new SelectQuery("MSFT_Disk", condition, selectedProperties));
            System.Management.EnumerationOptions enumOptions = new System.Management.EnumerationOptions();
            enumOptions.EnsureLocatable = true;
            ObjectSearcher.Options = enumOptions;
            return new DiskCollection(ObjectSearcher.Get());
        }
        
        [Browsable(true)]
        public static Disk CreateInstance() {
            System.Management.ManagementScope mgmtScope = null;
            if ((statMgmtScope == null)) {
                mgmtScope = new System.Management.ManagementScope();
                mgmtScope.Path.NamespacePath = CreatedWmiNamespace;
            }
            else {
                mgmtScope = statMgmtScope;
            }
            System.Management.ManagementPath mgmtPath = new System.Management.ManagementPath(CreatedClassName);
            System.Management.ManagementClass tmpMgmtClass = new System.Management.ManagementClass(mgmtScope, mgmtPath, null);
            return new Disk(tmpMgmtClass.CreateInstance());
        }
        
        [Browsable(true)]
        public void Delete() {
            PrivateLateBoundObject.Delete();
        }
        
        public uint Clear(bool RemoveData, bool RemoveOEM, bool ZeroOutEntireDisk, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("Clear");
                inParams["RemoveData"] = ((bool)(RemoveData));
                inParams["RemoveOEM"] = ((bool)(RemoveOEM));
                inParams["ZeroOutEntireDisk"] = ((bool)(ZeroOutEntireDisk));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Clear", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint ConvertStyle(ushort PartitionStyle, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("ConvertStyle");
                inParams["PartitionStyle"] = ((ushort)(PartitionStyle));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("ConvertStyle", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint CreatePartition(uint Alignment, bool AssignDriveLetter, char DriveLetter, string GptType, bool IsActive, bool IsHidden, ushort MbrType, ulong Offset, ulong Size, bool UseMaximumSize, out System.Management.ManagementBaseObject CreatedPartition, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("CreatePartition");
                inParams["Alignment"] = ((uint)(Alignment));
                inParams["AssignDriveLetter"] = ((bool)(AssignDriveLetter));
                inParams["DriveLetter"] = ((char)(DriveLetter));
                inParams["GptType"] = ((string)(GptType));
                inParams["IsActive"] = ((bool)(IsActive));
                inParams["IsHidden"] = ((bool)(IsHidden));
                inParams["MbrType"] = ((ushort)(MbrType));
                inParams["Offset"] = ((ulong)(Offset));
                inParams["Size"] = ((ulong)(Size));
                inParams["UseMaximumSize"] = ((bool)(UseMaximumSize));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("CreatePartition", inParams, null);
                CreatedPartition = ((System.Management.ManagementBaseObject)(outParams.Properties["CreatedPartition"].Value));
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                CreatedPartition = null;
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Initialize(ushort PartitionStyle, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("Initialize");
                inParams["PartitionStyle"] = ((ushort)(PartitionStyle));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Initialize", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Offline(out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Offline", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Online(out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Online", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Refresh(out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Refresh", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint SetAttributes(string Guid, bool IsReadOnly, uint Signature, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("SetAttributes");
                inParams["Guid"] = ((string)(Guid));
                inParams["IsReadOnly"] = ((bool)(IsReadOnly));
                inParams["Signature"] = ((uint)(Signature));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("SetAttributes", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public enum BusTypeValues {
            
            Unknown0 = 0,
            
            SCSI = 1,
            
            ATAPI = 2,
            
            ATA = 3,
            
            Val_1394 = 4,
            
            SSA = 5,
            
            Fibre_Channel = 6,
            
            USB = 7,
            
            RAID = 8,
            
            ISCSI = 9,
            
            SAS = 10,
            
            SATA = 11,
            
            SD = 12,
            
            MMC = 13,
            
            Virtual = 14,
            
            File_Backed_Virtual = 15,
            
            Storage_Spaces = 16,
            
            NVMe = 17,
            
            NULL_ENUM_VALUE = 18,
        }
        
        public enum HealthStatusValues {
            
            Unknown0 = 0,
            
            Healthy = 1,
            
            Failing = 4,
            
            Failed = 8,
            
            NULL_ENUM_VALUE = 9,
        }
        
        public enum OfflineReasonValues {
            
            Policy = 1,
            
            Redundant_Path = 2,
            
            Snapshot = 3,
            
            Collision = 4,
            
            Resource_Exhaustion = 5,
            
            Critical_Write_Failures = 6,
            
            Data_Integrity_Scan_Required = 7,
            
            NULL_ENUM_VALUE = 0,
        }
        
        public enum OperationalStatusValues {
            
            Unknown0 = 0,
            
            Online0 = 1,
            
            Not_Ready = 2,
            
            No_Media = 3,
            
            Offline0 = 4,
            
            Failed = 5,
            
            Missing = 6,
            
            NULL_ENUM_VALUE = 7,
        }
        
        public enum PartitionStyleValues {
            
            Unknown0 = 0,
            
            MBR = 1,
            
            GPT = 2,
            
            NULL_ENUM_VALUE = 3,
        }
        
        public enum ProvisioningTypeValues {
            
            Unknown0 = 0,
            
            Thin = 1,
            
            Fixed = 2,
            
            NULL_ENUM_VALUE = 3,
        }
        
        public enum UniqueIdFormatValues {
            
            Vendor_Specific = 0,
            
            Vendor_Id = 1,
            
            EUI64 = 2,
            
            FCPH_Name = 3,
            
            SCSI_Name_String = 8,
            
            NULL_ENUM_VALUE = 9,
        }
        
        // Enumerator implementation for enumerating instances of the class.
        public class DiskCollection : object, ICollection {
            
            private ManagementObjectCollection privColObj;
            
            public DiskCollection(ManagementObjectCollection objCollection) {
                privColObj = objCollection;
            }
            
            public virtual int Count {
                get {
                    return privColObj.Count;
                }
            }
            
            public virtual bool IsSynchronized {
                get {
                    return privColObj.IsSynchronized;
                }
            }
            
            public virtual object SyncRoot {
                get {
                    return this;
                }
            }
            
            public virtual void CopyTo(System.Array array, int index) {
                privColObj.CopyTo(array, index);
                int nCtr;
                for (nCtr = 0; (nCtr < array.Length); nCtr = (nCtr + 1)) {
                    array.SetValue(new Disk(((System.Management.ManagementObject)(array.GetValue(nCtr)))), nCtr);
                }
            }
            
            public virtual System.Collections.IEnumerator GetEnumerator() {
                return new DiskEnumerator(privColObj.GetEnumerator());
            }
            
            public class DiskEnumerator : object, System.Collections.IEnumerator {
                
                private ManagementObjectCollection.ManagementObjectEnumerator privObjEnum;
                
                public DiskEnumerator(ManagementObjectCollection.ManagementObjectEnumerator objEnum) {
                    privObjEnum = objEnum;
                }
                
                public virtual object Current {
                    get {
                        return new Disk(((System.Management.ManagementObject)(privObjEnum.Current)));
                    }
                }
                
                public virtual bool MoveNext() {
                    return privObjEnum.MoveNext();
                }
                
                public virtual void Reset() {
                    privObjEnum.Reset();
                }
            }
        }
        
        // TypeConverter to handle null values for ValueType properties
        public class WMIValueTypeConverter : TypeConverter {
            
            private TypeConverter baseConverter;
            
            private System.Type baseType;
            
            public WMIValueTypeConverter(System.Type inBaseType) {
                baseConverter = TypeDescriptor.GetConverter(inBaseType);
                baseType = inBaseType;
            }
            
            public override bool CanConvertFrom(System.ComponentModel.ITypeDescriptorContext context, System.Type srcType) {
                return baseConverter.CanConvertFrom(context, srcType);
            }
            
            public override bool CanConvertTo(System.ComponentModel.ITypeDescriptorContext context, System.Type destinationType) {
                return baseConverter.CanConvertTo(context, destinationType);
            }
            
            public override object ConvertFrom(System.ComponentModel.ITypeDescriptorContext context, System.Globalization.CultureInfo culture, object value) {
                return baseConverter.ConvertFrom(context, culture, value);
            }
            
            public override object CreateInstance(System.ComponentModel.ITypeDescriptorContext context, System.Collections.IDictionary dictionary) {
                return baseConverter.CreateInstance(context, dictionary);
            }
            
            public override bool GetCreateInstanceSupported(System.ComponentModel.ITypeDescriptorContext context) {
                return baseConverter.GetCreateInstanceSupported(context);
            }
            
            public override PropertyDescriptorCollection GetProperties(System.ComponentModel.ITypeDescriptorContext context, object value, System.Attribute[] attributeVar) {
                return baseConverter.GetProperties(context, value, attributeVar);
            }
            
            public override bool GetPropertiesSupported(System.ComponentModel.ITypeDescriptorContext context) {
                return baseConverter.GetPropertiesSupported(context);
            }
            
            public override System.ComponentModel.TypeConverter.StandardValuesCollection GetStandardValues(System.ComponentModel.ITypeDescriptorContext context) {
                return baseConverter.GetStandardValues(context);
            }
            
            public override bool GetStandardValuesExclusive(System.ComponentModel.ITypeDescriptorContext context) {
                return baseConverter.GetStandardValuesExclusive(context);
            }
            
            public override bool GetStandardValuesSupported(System.ComponentModel.ITypeDescriptorContext context) {
                return baseConverter.GetStandardValuesSupported(context);
            }
            
            public override object ConvertTo(System.ComponentModel.ITypeDescriptorContext context, System.Globalization.CultureInfo culture, object value, System.Type destinationType) {
                if ((baseType.BaseType == typeof(System.Enum))) {
                    if ((value.GetType() == destinationType)) {
                        return value;
                    }
                    if ((((value == null) 
                                && (context != null)) 
                                && (context.PropertyDescriptor.ShouldSerializeValue(context.Instance) == false))) {
                        return  "NULL_ENUM_VALUE" ;
                    }
                    return baseConverter.ConvertTo(context, culture, value, destinationType);
                }
                if (((baseType == typeof(bool)) 
                            && (baseType.BaseType == typeof(System.ValueType)))) {
                    if ((((value == null) 
                                && (context != null)) 
                                && (context.PropertyDescriptor.ShouldSerializeValue(context.Instance) == false))) {
                        return "";
                    }
                    return baseConverter.ConvertTo(context, culture, value, destinationType);
                }
                if (((context != null) 
                            && (context.PropertyDescriptor.ShouldSerializeValue(context.Instance) == false))) {
                    return "";
                }
                return baseConverter.ConvertTo(context, culture, value, destinationType);
            }
        }
        
        // Embedded class to represent WMI system Properties.
        [TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
        public class ManagementSystemProperties {
            
            private System.Management.ManagementBaseObject PrivateLateBoundObject;
            
            public ManagementSystemProperties(System.Management.ManagementBaseObject ManagedObject) {
                PrivateLateBoundObject = ManagedObject;
            }
            
            [Browsable(true)]
            public int GENUS {
                get {
                    return ((int)(PrivateLateBoundObject["__GENUS"]));
                }
            }
            
            [Browsable(true)]
            public string CLASS {
                get {
                    return ((string)(PrivateLateBoundObject["__CLASS"]));
                }
            }
            
            [Browsable(true)]
            public string SUPERCLASS {
                get {
                    return ((string)(PrivateLateBoundObject["__SUPERCLASS"]));
                }
            }
            
            [Browsable(true)]
            public string DYNASTY {
                get {
                    return ((string)(PrivateLateBoundObject["__DYNASTY"]));
                }
            }
            
            [Browsable(true)]
            public string RELPATH {
                get {
                    return ((string)(PrivateLateBoundObject["__RELPATH"]));
                }
            }
            
            [Browsable(true)]
            public int PROPERTY_COUNT {
                get {
                    return ((int)(PrivateLateBoundObject["__PROPERTY_COUNT"]));
                }
            }
            
            [Browsable(true)]
            public string[] DERIVATION {
                get {
                    return ((string[])(PrivateLateBoundObject["__DERIVATION"]));
                }
            }
            
            [Browsable(true)]
            public string SERVER {
                get {
                    return ((string)(PrivateLateBoundObject["__SERVER"]));
                }
            }
            
            [Browsable(true)]
            public string NAMESPACE {
                get {
                    return ((string)(PrivateLateBoundObject["__NAMESPACE"]));
                }
            }
            
            [Browsable(true)]
            public string PATH {
                get {
                    return ((string)(PrivateLateBoundObject["__PATH"]));
                }
            }
        }
    }
}
