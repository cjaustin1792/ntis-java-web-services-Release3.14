/*
	Copyright (C) 2012 Thales Transportation Systems UK
	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
	to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
	and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
	IN THE SOFTWARE.
 */

package com.thales.ntis.subscriber.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.DeliverAverageJourneyTimeRequest;
import com.thales.ntis.subscriber.datex.DeliverAverageJourneyTimeResponse;
import com.thales.ntis.subscriber.datex.FusedData;
import com.thales.ntis.subscriber.datex.FusedDataPublication;
import com.thales.ntis.subscriber.datex.ProcessedTrafficData;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class AverageJourneyTimeServiceImpl extends AbstractDatexService
        implements AverageJourneyTimeService {

    private static final Logger LOG = LoggerFactory
            .getLogger(AverageJourneyTimeServiceImpl.class);

    @Override
    public DeliverAverageJourneyTimeResponse handle(
            DeliverAverageJourneyTimeRequest request) {

        LOG.info("NEW DeliverAverageJourneyTimeRequest RECEIVED!");

        D2LogicalModel d2LogicalModel = request.getD2LogicalModel();
        FusedDataPublication fusedDataPublication = null;

        // Validate the D2Logical Model
        if (!validate(d2LogicalModel)) {
            throw new RuntimeException(
                    "Incoming request does not appear to be valid!");
        }

        // FusedDataPublication contains the journey time, direction, code,
        // region etc.
        try {
            fusedDataPublication = (FusedDataPublication) d2LogicalModel
                    .getPayloadPublication();

            // You could use the FusedDataPublication and extract the
            // corresponding fields.
            if (fusedDataPublication != null
                    && fusedDataPublication.getFusedData().get(0) != null) {
                FusedData fusedData = fusedDataPublication.getFusedData().get(0);
                List<ProcessedTrafficData> trafficDataList = fusedData.getLinkData();
                if (trafficDataList.size() > 0) {
                    LOG.info("JournetTime sec is " + trafficDataList.get(0).getJourneyTimeSec());
                    LOG.info("Speed in Kph is " + trafficDataList.get(0).getSpeedKph());
                }
                LOG.info("createdUtc is " + fusedData.getCreatedUtc().toString());
                LOG.info("Max arrival Utc " + fusedData.getMaxArrivalUtc().toString());

            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        DeliverAverageJourneyTimeResponse response = new DeliverAverageJourneyTimeResponse();
        response.setStatus("DeliverAverageJourneyTimeRequest: Successful Delivery");

        return response;
    }
}
