/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.queue;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.JMSException;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.ejb3.mdb.ProducerManager;
import org.jboss.ejb3.mdb.ProducerObject;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.core.queue.i.QueuerRemote;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Queuer")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class QueuerBean implements Queuer, QueuerRemote
{
	@Resource(mappedName=Inbound.JNDI_NAME) Inbound inbound;
	@Resource(mappedName=Outbound.JNDI_NAME) Outbound outbound;
	
	/**
	 * @see Queuer#queueForDelivery(Long)
	 */
	public void queueForDelivery(Long mailId)
	{
		try
		{
			ProducerManager manager = (ProducerManager)((ProducerObject)inbound).getProducerManager();
			manager.connect();
			try
			{
				inbound.deliver(mailId);
			}
			finally
			{
				manager.close();
			}
		}
		catch (JMSException ex) { throw new EJBException(ex); }
	}
	
	/**
	 * @see Queuer#queueForDelivery(Long, Long)
	 */
	public void queueForDelivery(Long mailId, Long personId)
	{
		try
		{
			ProducerManager manager = (ProducerManager)((ProducerObject)outbound).getProducerManager();
			manager.connect();
			try
			{
				outbound.deliver(mailId, personId);
			}
			finally
			{
				manager.close();
			}
		}
		catch (JMSException ex) { throw new EJBException(ex); }
	}
}
