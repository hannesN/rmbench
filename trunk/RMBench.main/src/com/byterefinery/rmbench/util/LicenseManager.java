/*
 * created 10.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.preferences.PreferenceHandler;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultKeyStoreParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.license.NoLicenseInstalledException;

/**
 * the LicenseManager handles installing and validating license certificates
 * 
 * @author cse
 */
public class LicenseManager {

    private static final String TRIAL_REJECTED_MARKER = 
        decode("selrbm!eniu eibvoi!:eeucfjfr!etnfcjl!lbist");
    private static final String TRIAL_REJECTED_REINSTALL = 
        decode("tqmftua!lmausoifr!fp fsvadec eeucfjfr!etnfcjl!lbist");
    
    private static final String SUBJECT = "RMBench";
    private static final String VERSION = "1.0.6";
    private static final String STOREPASSWD = "Huflatt66ich233";
    private static final String CIPHERPASSWD = "jkhasdkjasdUZIUZ987987--";
    private static final String TYPE_TRIAL = "Trial";
    private static final String TYPE_USER = "User";
    
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    
    private static final int MAX_TABLES = 40;
    private static final int MAX_DIAGRAMS = 3;
    
    /*
     * overridden to remove schlichtherle resources stuff
     */
    public static class LicenseException extends LicenseContentException {
        private static final long serialVersionUID = 1L;
        
        public LicenseException(String reason) {
            super(reason);
        }
        public String getLocalizedMessage() {
            return super.getMessage();
        }
    }
    
    /*
     * subclassed to override the validate method
     */
    private static final class TruelicLicenseManager extends de.schlichtherle.license.LicenseManager {
        
        public TruelicLicenseManager(LicenseParam param) {
            super(param);
        }

        /*
         * mostly copied from de.schlichtherle.license.LicenseManager. Bad design
         */
        protected synchronized void validate(final LicenseContent content)
            throws LicenseContentException {
            
            final LicenseParam param = getLicenseParam();
            if (!param.getSubject().equals(content.getSubject()))
                throw new LicenseException("invalidSubject");
            if (content.getHolder() == null)
                throw new LicenseException("holderIsNull");
            if (content.getIssuer() == null)
                throw new LicenseException("issuerIsNull");
            if (content.getIssued() == null)
                throw new LicenseException("issuedIsNull");
            final Date now = new Date();
            final Date notBefore = content.getNotBefore();
            if (notBefore != null && now.before(notBefore))
                throw new LicenseException("licenseIsNotYetValid");
            final Date notAfter = content.getNotAfter();
            if (notAfter != null && now.after(notAfter))
                throw new LicenseException("licenseHasExpired");
            final String consumerType = content.getConsumerType();
            if (consumerType == null)
                throw new LicenseException("consumerTypeIsNull");
            
            if(consumerType.equals(TYPE_TRIAL)) {
                //if its a trial license, the version must match exactly
                if(!((String)content.getExtra()).equals(VERSION))
                    throw new LicenseException("Version does not match");
            }
            else {
                //for real license, only the first digit must match
                String licVer = (String)content.getExtra();
                if(licVer.length() == 0 || licVer.charAt(0) != VERSION.charAt(0))
                    throw new LicenseException("Version does not match");
            }
            final Preferences prefs = param.getPreferences();
            if (prefs != null && prefs.isUserNode()) {
                if (!TYPE_USER.equalsIgnoreCase(consumerType) && !TYPE_TRIAL.equalsIgnoreCase(consumerType))
                    throw new LicenseException("consumerTypeIsNotUser");
                if (content.getConsumerAmount() != 1)
                    throw new LicenseException("consumerAmountIsNotOne");
            } else {
                if (content.getConsumerAmount() <= 0)
                    throw new LicenseException("consumerAmountIsNotPositive");
            }
        }
    }
    private TruelicLicenseManager manager;
    private LicenseContent license;
    private Properties licenseProperties; //cached from license
    
    /*
     * perform verification in a background thread
     */
    private Runnable verifier = new Runnable() {
        public void run() {
            try {
                //let the plugin start up completely
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
            try {
                setLicense(manager.verify());
                verifyTrialLicense();
            }
            catch (LicenseException e) {
            	RMBenchPlugin.logWarning(e.getMessage());
            }
            catch (NoLicenseInstalledException e) {
                RMBenchPlugin.logInfo("RMBench: no license installed");
            }
            catch(Exception x) {
            	RMBenchPlugin.logError(x);
            }
        }
    };
    
    /**
     * activate the license manager. A background thread is started to retrieve and
     * validate the license 
     */
    public void activate() {
        KeyStoreParam keyStoreParam = new DefaultKeyStoreParam(
                IPublicStore.class, 
                IPublicStore.NAME,
                SUBJECT, 
                STOREPASSWD, 
                null);
        CipherParam cipherParam = new DefaultCipherParam(CIPHERPASSWD);
        Preferences licPrefs = Preferences.userNodeForPackage(LicenseManager.class).node(VERSION);
        LicenseParam param = new DefaultLicenseParam(SUBJECT, licPrefs, keyStoreParam, cipherParam);
        manager = new TruelicLicenseManager(param);
        
        Thread thread = new Thread(verifier);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * deactivate the license manager
     */
    public void deactivate() {
    }

    /**
     * @return the contents of the info attribute of the license, or <code>null</code>
     */
    public Properties getInfo() {
        return licenseProperties;
    }

    /**
     * @param path a valid file system path
     * @throws LicenseException if license could not be validated and installed
     */
    public void installLicenseFile(String path) throws LicenseException {
        try {
            setLicense(manager.install(new File(path)));
            verifyTrialLicense();
        }
        catch (Exception e) {
            RMBenchPlugin.logError("Error installing license file", e);
            throw new LicenseException(e.getMessage());
        }
    }

    private void setLicense(LicenseContent content) {
        license = content;
        licenseProperties = new Properties();
        String info = license.getInfo();
        try {
            licenseProperties.load(new ByteArrayInputStream(info.getBytes()));
        } catch (IOException e) {
            RMBenchPlugin.logError("Error reading license properties", e);
        }
    }

    /**
     * verify that the current license is a not a re-installed trial license. This
     * is to prevent users from installing new trial licenses after each expiry period.
     * <p/>
     * The issue date of the first installed trial license is stored in a preference. 
     * If that value is already present, the currently installed trial license is rejected
     * if its issue date is after the stored date.
     */
    private void verifyTrialLicense() {
        if(isTrialLicense()) {
            String lastTrial = PreferenceHandler.getLastTrial(VERSION);
            if(lastTrial != null) {
                try {
                    long lastMillis = Long.parseLong(lastTrial);
                    Date lastDate = new Date(lastMillis);
                    
                    if(license.getIssued().after(lastDate)) {
                        RMBenchPlugin.logWarning(TRIAL_REJECTED_REINSTALL);
                        setLicense(null);
                    }
                } catch(NumberFormatException x) {
                    RMBenchPlugin.logWarning(TRIAL_REJECTED_MARKER);
                    setLicense(null);
                }
            }
            else {
                String value = String.valueOf(license.getIssued().getTime());
                PreferenceHandler.setLastTrial(VERSION, value);
            }
        }
    }

    /**
     * @return <code>true</code>if this is a per user commercial license
     */
    public boolean isUserLicense() {
        return license != null && TYPE_USER.equals(license.getConsumerType());
    }

    /**
     * @return <code>true</code>if this is a trial license
     */
    public boolean isTrialLicense() {
        return license != null && TYPE_TRIAL.equals(license.getConsumerType());
    }

    /**
     * @return <code>true</code>if this is an unlicensed copy (community edition)
     */
    public boolean isUnlicensed() {
        return license == null;
    }
    
    /**
     * @return the number of days remaining in the trial period, if this is a trial license,
     * or 0 otherwise
     */
    public int getTrialDays() {
        if(isTrialLicense()) {
            Date notAfter = license.getNotAfter();
            Calendar end = Calendar.getInstance();
            end.setTime(notAfter);
            
            Calendar now = Calendar.getInstance();
            return now.before(end) ? (int)((end.getTimeInMillis() - now.getTimeInMillis()) / MILLIS_PER_DAY) : 0; 
        }
        return 0;
    }
    

    /**
     * @return the maximum number of allowed tables if this is an unlicensed copy, or 
     * {@link Integer#MAX_VALUE}
     */
    public int getMaxTables() {
        return isUnlicensed() ? MAX_TABLES : Integer.MAX_VALUE;
    }
    
    /**
     * @return the maximum number of allowed diagrams if this is an unlicensed copy, or
     * {@link Integer#MAX_VALUE}
     */
    public int getMaxDiagrams() {
        return isUnlicensed() ? MAX_DIAGRAMS : Integer.MAX_VALUE;
    }
    
    /**
     * this method may open a message box to inform the user of a failed check. If that is not 
     * possible from the current thread, it will write to the log.
     * 
     * @param tableCount the current number of tables in a model
     * @return true if the given number is >= the maximum limit of tables for the current license
     */
    public boolean checkMaxTables(int tableCount) {
        if (!isUnlicensed())
            return false;
        
        if(tableCount >= getMaxTables()) {
            
            Shell shell = Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : null;
            if(shell != null)
                MessageDialog.openInformation(shell, 
                        RMBenchMessages.License_Title,
                        RMBenchMessages.License_MaxTables);
            else
                RMBenchPlugin.logWarning(RMBenchMessages.License_MaxTables);
            
            return true;
        }
        return false;
    }

    /**
     * this method may open a message box to inform the user of a failed check. If that is not 
     * possible from the current thread, it will write to the log.
     * 
     * @param diagramCount the current number of diagrams in a model
     * @return true if the given number is >= to the maximum limit of diagrams for the current license
     */
    public boolean checkMaxDiagrams(int diagramCount) {
        if(diagramCount >= getMaxDiagrams()) {
            
            Shell shell = Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : null;
            if(shell != null)
                MessageDialog.openInformation(shell, 
                        RMBenchMessages.License_Title,
                        RMBenchMessages.License_MaxDiagrams);
            else
                RMBenchPlugin.logWarning(RMBenchMessages.License_MaxDiagrams);
            
            return true;
        }
        return false;
    }
    
    /**
     * @param value encoded value
     * @return decded value
     * @see #encode(String)
     */
    public static String decode(String value) {
        StringBuffer buf = new StringBuffer();
        int diff = value.length()+1 % 2;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if((i+diff)%2 > 0)
                c--;
            buf.insert(0, c);
        }
        return buf.toString();
    }
    
    /**
     * simple string encoding
     * @param value string to encode
     * @return encoded string
     * @see #decode(String)
     */
    public static String encode(String value) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if(i%2 > 0)
                c++;
            buf.insert(0, c);
        }
        return buf.toString();
    }
}
