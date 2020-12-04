/*
 * Copyright 2020 Google LLC
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google LLC nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.google.api.gax.rpc.mtls;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.gax.rpc.mtls.MtlsProvider.UseMtlsEndpoint;
import com.google.api.gax.rpc.testing.FakeMtlsProvider;
import java.io.IOException;
import org.junit.Test;

public abstract class AbstractMtlsTransportChannelTest {
  /**
   * Returns the mTLS object from the created transport channel. For HttpJsonTransportChannel, the
   * mTLS object is the SslContext; for GrpcTransportChannel, the mTLS object is the HttpTransport.
   * The transport channel is mTLS if and only if the related mTLS object is not null.
   */
  protected abstract Object getMtlsObjectFromTransportChannel(MtlsProvider provider)
      throws IOException;

  @Test
  public void testNotUseClientCertificate() throws IOException {
    MtlsProvider provider = new FakeMtlsProvider(false, UseMtlsEndpoint.AUTO, null, "", false);
    assertNull(getMtlsObjectFromTransportChannel(provider));
  }

  @Test
  public void testUseClientCertificate() throws IOException {
    MtlsProvider provider =
        new FakeMtlsProvider(
            true, UseMtlsEndpoint.AUTO, FakeMtlsProvider.createTestMtlsKeyStore(), "", false);
    assertNotNull(getMtlsObjectFromTransportChannel(provider));
  }

  @Test
  public void testNoClientCertificate() throws IOException {
    MtlsProvider provider = new FakeMtlsProvider(true, UseMtlsEndpoint.AUTO, null, "", false);
    assertNull(getMtlsObjectFromTransportChannel(provider));
  }

  @Test
  public void testGetKeyStoreThrows() {
    // Test the case where provider.getKeyStore() throws.
    MtlsProvider provider = new FakeMtlsProvider(true, UseMtlsEndpoint.AUTO, null, "", true);
    try {
      getMtlsObjectFromTransportChannel(provider);
      fail("should throw and exception");
    } catch (IOException e) {
      assertTrue(
          "expected to fail with exception",
          e.getMessage().contains("getKeyStore throws exception"));
    }
  }
}
