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
    // An Early Bound class generated for the WMI class.MSFT_Volume
    public class Volume : System.ComponentModel.Component {
        
        // Private property to hold the WMI namespace in which the class resides.
        private static string CreatedWmiNamespace = "ROOT\\microsoft\\windows\\storage";
        
        // Private property to hold the name of WMI class which created this class.
        private static string CreatedClassName = "MSFT_Volume";
        
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
        public Volume() {
            this.InitializeObject(null, null, null);
        }
        
        public Volume(string keyObjectId) {
            this.InitializeObject(null, new System.Management.ManagementPath(Volume.ConstructPath(keyObjectId)), null);
        }
        
        public Volume(System.Management.ManagementScope mgmtScope, string keyObjectId) {
            this.InitializeObject(((System.Management.ManagementScope)(mgmtScope)), new System.Management.ManagementPath(Volume.ConstructPath(keyObjectId)), null);
        }
        
        public Volume(System.Management.ManagementPath path, System.Management.ObjectGetOptions getOptions) {
            this.InitializeObject(null, path, getOptions);
        }
        
        public Volume(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path) {
            this.InitializeObject(mgmtScope, path, null);
        }
        
        public Volume(System.Management.ManagementPath path) {
            this.InitializeObject(null, path, null);
        }
        
        public Volume(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path, System.Management.ObjectGetOptions getOptions) {
            this.InitializeObject(mgmtScope, path, getOptions);
        }
        
        public Volume(System.Management.ManagementObject theObject) {
            Initialize();
            if ((CheckIfProperClass(theObject) == true)) {
                PrivateLateBoundObject = theObject;
                PrivateSystemProperties = new ManagementSystemProperties(PrivateLateBoundObject);
                curObj = PrivateLateBoundObject;
            }
            else {
                throw new System.ArgumentException("Class name does not match.");
            }
        }
        
        public Volume(System.Management.ManagementBaseObject theObject) {
            Initialize();
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
                return "ROOT\\microsoft\\windows\\storage";
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
        public bool IsDriveLetterNull {
            get {
                if ((curObj["DriveLetter"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Drive letter assigned to the volume.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public char DriveLetter {
            get {
                if ((curObj["DriveLetter"] == null)) {
                    return System.Convert.ToChar(0);
                }
                return ((char)(curObj["DriveLetter"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsDriveTypeNull {
            get {
                if ((curObj["DriveType"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Denotes the type of the volume.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public DriveTypeValues DriveType {
            get {
                if ((curObj["DriveType"] == null)) {
                    return ((DriveTypeValues)(System.Convert.ToInt32(7)));
                }
                return ((DriveTypeValues)(System.Convert.ToInt32(curObj["DriveType"])));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("File system on the volume.")]
        public string FileSystem {
            get {
                return ((string)(curObj["FileSystem"]));
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("File system label of the volume.")]
        public string FileSystemLabel {
            get {
                return ((string)(curObj["FileSystemLabel"]));
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
        [Description("Denotes the health of the volume.")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public HealthStatusValues HealthStatus {
            get {
                if ((curObj["HealthStatus"] == null)) {
                    return ((HealthStatusValues)(System.Convert.ToInt32(4)));
                }
                return ((HealthStatusValues)(System.Convert.ToInt32(curObj["HealthStatus"])));
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
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Guid path of the volume.")]
        public string Path0 {
            get {
                return ((string)(curObj["Path"]));
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
        [Description("Total size of the volume")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public ulong Size {
            get {
                if ((curObj["Size"] == null)) {
                    return System.Convert.ToUInt64(0);
                }
                return ((ulong)(curObj["Size"]));
            }
        }
        
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public bool IsSizeRemainingNull {
            get {
                if ((curObj["SizeRemaining"] == null)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("Available space on the volume")]
        [TypeConverter(typeof(WMIValueTypeConverter))]
        public ulong SizeRemaining {
            get {
                if ((curObj["SizeRemaining"] == null)) {
                    return System.Convert.ToUInt64(0);
                }
                return ((ulong)(curObj["SizeRemaining"]));
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
        
        private bool ShouldSerializeDriveLetter() {
            if ((this.IsDriveLetterNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeDriveType() {
            if ((this.IsDriveTypeNull == false)) {
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
        
        private bool ShouldSerializeSize() {
            if ((this.IsSizeNull == false)) {
                return true;
            }
            return false;
        }
        
        private bool ShouldSerializeSizeRemaining() {
            if ((this.IsSizeRemainingNull == false)) {
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
        
        private void Initialize() {
            AutoCommitProp = true;
            isEmbedded = false;
        }
        
        private static string ConstructPath(string keyObjectId) {
            string strPath = "ROOT\\microsoft\\windows\\storage:MSFT_Volume";
            strPath = string.Concat(strPath, string.Concat(".ObjectId=", string.Concat("\"", string.Concat(keyObjectId, "\""))));
            return strPath;
        }
        
        private void InitializeObject(System.Management.ManagementScope mgmtScope, System.Management.ManagementPath path, System.Management.ObjectGetOptions getOptions) {
            Initialize();
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
        public static VolumeCollection GetInstances() {
            return GetInstances(null, null, null);
        }
        
        public static VolumeCollection GetInstances(string condition) {
            return GetInstances(null, condition, null);
        }
        
        public static VolumeCollection GetInstances(string[] selectedProperties) {
            return GetInstances(null, null, selectedProperties);
        }
        
        public static VolumeCollection GetInstances(string condition, string[] selectedProperties) {
            return GetInstances(null, condition, selectedProperties);
        }
        
        public static VolumeCollection GetInstances(System.Management.ManagementScope mgmtScope, System.Management.EnumerationOptions enumOptions) {
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
            pathObj.ClassName = "MSFT_Volume";
            pathObj.NamespacePath = "root\\Microsoft\\Windows\\Storage";
            System.Management.ManagementClass clsObject = new System.Management.ManagementClass(mgmtScope, pathObj, null);
            if ((enumOptions == null)) {
                enumOptions = new System.Management.EnumerationOptions();
                enumOptions.EnsureLocatable = true;
            }
            return new VolumeCollection(clsObject.GetInstances(enumOptions));
        }
        
        public static VolumeCollection GetInstances(System.Management.ManagementScope mgmtScope, string condition) {
            return GetInstances(mgmtScope, condition, null);
        }
        
        public static VolumeCollection GetInstances(System.Management.ManagementScope mgmtScope, string[] selectedProperties) {
            return GetInstances(mgmtScope, null, selectedProperties);
        }
        
        public static VolumeCollection GetInstances(System.Management.ManagementScope mgmtScope, string condition, string[] selectedProperties) {
            if ((mgmtScope == null)) {
                if ((statMgmtScope == null)) {
                    mgmtScope = new System.Management.ManagementScope();
                    mgmtScope.Path.NamespacePath = "root\\Microsoft\\Windows\\Storage";
                }
                else {
                    mgmtScope = statMgmtScope;
                }
            }
            System.Management.ManagementObjectSearcher ObjectSearcher = new System.Management.ManagementObjectSearcher(mgmtScope, new SelectQuery("MSFT_Volume", condition, selectedProperties));
            System.Management.EnumerationOptions enumOptions = new System.Management.EnumerationOptions();
            enumOptions.EnsureLocatable = true;
            ObjectSearcher.Options = enumOptions;
            return new VolumeCollection(ObjectSearcher.Get());
        }
        
        [Browsable(true)]
        public static Volume CreateInstance() {
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
            return new Volume(tmpMgmtClass.CreateInstance());
        }
        
        [Browsable(true)]
        public void Delete() {
            PrivateLateBoundObject.Delete();
        }
        
        public uint Flush() {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Flush", inParams, null);
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Format(uint AllocationUnitSize, bool Compress, bool DisableHeatGathering, string FileSystem, string FileSystemLabel, bool Force, bool Full, bool SetIntegrityStreams, bool ShortFileNameSupport, bool UseLargeFRS, out System.Management.ManagementBaseObject ExtendedStatus, out System.Management.ManagementBaseObject FormattedVolume) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("Format");
                inParams["AllocationUnitSize"] = ((uint)(AllocationUnitSize));
                inParams["Compress"] = ((bool)(Compress));
                inParams["DisableHeatGathering"] = ((bool)(DisableHeatGathering));
                inParams["FileSystem"] = ((string)(FileSystem));
                inParams["FileSystemLabel"] = ((string)(FileSystemLabel));
                inParams["Force"] = ((bool)(Force));
                inParams["Full"] = ((bool)(Full));
                inParams["SetIntegrityStreams"] = ((bool)(SetIntegrityStreams));
                inParams["ShortFileNameSupport"] = ((bool)(ShortFileNameSupport));
                inParams["UseLargeFRS"] = ((bool)(UseLargeFRS));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Format", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                FormattedVolume = ((System.Management.ManagementBaseObject)(outParams.Properties["FormattedVolume"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                FormattedVolume = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint GetAttributes(out bool VolumeScrubEnabled) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("GetAttributes", inParams, null);
                VolumeScrubEnabled = System.Convert.ToBoolean(outParams.Properties["VolumeScrubEnabled"].Value);
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                VolumeScrubEnabled = System.Convert.ToBoolean(0);
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint GetCorruptionCount(out uint CorruptionCount, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("GetCorruptionCount", inParams, null);
                CorruptionCount = System.Convert.ToUInt32(outParams.Properties["CorruptionCount"].Value);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                CorruptionCount = System.Convert.ToUInt32(0);
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint GetSupportedClusterSizes(string FileSystem, out System.Management.ManagementBaseObject ExtendedStatus, out uint[] SupportedClusterSizes) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("GetSupportedClusterSizes");
                inParams["FileSystem"] = ((string)(FileSystem));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("GetSupportedClusterSizes", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                SupportedClusterSizes = ((uint[])(outParams.Properties["SupportedClusterSizes"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                SupportedClusterSizes = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint GetSupportedFileSystems(out System.Management.ManagementBaseObject ExtendedStatus, out string[] SupportedFileSystems) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("GetSupportedFileSystems", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                SupportedFileSystems = ((string[])(outParams.Properties["SupportedFileSystems"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                SupportedFileSystems = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Optimize(bool Analyze, bool Defrag, bool ReTrim, bool SlabConsolidate, bool TierOptimize, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("Optimize");
                inParams["Analyze"] = ((bool)(Analyze));
                inParams["Defrag"] = ((bool)(Defrag));
                inParams["ReTrim"] = ((bool)(ReTrim));
                inParams["SlabConsolidate"] = ((bool)(SlabConsolidate));
                inParams["TierOptimize"] = ((bool)(TierOptimize));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Optimize", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint Repair(bool OfflineScanAndFix, bool Scan, bool SpotFix, out System.Management.ManagementBaseObject ExtendedStatus, out uint Output) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("Repair");
                inParams["OfflineScanAndFix"] = ((bool)(OfflineScanAndFix));
                inParams["Scan"] = ((bool)(Scan));
                inParams["SpotFix"] = ((bool)(SpotFix));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("Repair", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                Output = System.Convert.ToUInt32(outParams.Properties["Output"].Value);
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                Output = System.Convert.ToUInt32(0);
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint SetAttributes(bool EnableVolumeScrub, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("SetAttributes");
                inParams["EnableVolumeScrub"] = ((bool)(EnableVolumeScrub));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("SetAttributes", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public uint SetFileSystemLabel(string FileSystemLabel, out System.Management.ManagementBaseObject ExtendedStatus) {
            if ((isEmbedded == false)) {
                System.Management.ManagementBaseObject inParams = null;
                inParams = PrivateLateBoundObject.GetMethodParameters("SetFileSystemLabel");
                inParams["FileSystemLabel"] = ((string)(FileSystemLabel));
                System.Management.ManagementBaseObject outParams = PrivateLateBoundObject.InvokeMethod("SetFileSystemLabel", inParams, null);
                ExtendedStatus = ((System.Management.ManagementBaseObject)(outParams.Properties["ExtendedStatus"].Value));
                return System.Convert.ToUInt32(outParams.Properties["ReturnValue"].Value);
            }
            else {
                ExtendedStatus = null;
                return System.Convert.ToUInt32(0);
            }
        }
        
        public enum DriveTypeValues {
            
            Unknown0 = 0,
            
            Invalid_Root_Path = 1,
            
            Removable = 2,
            
            Fixed = 3,
            
            Remote = 4,
            
            CD_ROM = 5,
            
            RAM_Disk = 6,
            
            NULL_ENUM_VALUE = 7,
        }
        
        public enum HealthStatusValues {
            
            Healthy = 0,
            
            Scan_Needed = 1,
            
            Spot_Fix_Needed = 2,
            
            Full_Repair_Needed = 3,
            
            NULL_ENUM_VALUE = 4,
        }
        
        // Enumerator implementation for enumerating instances of the class.
        public class VolumeCollection : object, ICollection {
            
            private ManagementObjectCollection privColObj;
            
            public VolumeCollection(ManagementObjectCollection objCollection) {
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
                    array.SetValue(new Volume(((System.Management.ManagementObject)(array.GetValue(nCtr)))), nCtr);
                }
            }
            
            public virtual System.Collections.IEnumerator GetEnumerator() {
                return new VolumeEnumerator(privColObj.GetEnumerator());
            }
            
            public class VolumeEnumerator : object, System.Collections.IEnumerator {
                
                private ManagementObjectCollection.ManagementObjectEnumerator privObjEnum;
                
                public VolumeEnumerator(ManagementObjectCollection.ManagementObjectEnumerator objEnum) {
                    privObjEnum = objEnum;
                }
                
                public virtual object Current {
                    get {
                        return new Volume(((System.Management.ManagementObject)(privObjEnum.Current)));
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
